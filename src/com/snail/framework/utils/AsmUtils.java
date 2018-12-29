package com.snail.framework.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class AsmUtils {
	 /**
     * 
     * <p>
     * �Ƚϲ��������Ƿ�һ��
     * </p>
     * 
     * @param types
     *            asm������({@link Type})
     * @param clazzes
     *            java ����({@link Class})
     * @return
     */
    private static boolean sameType(Type[] types, Class<?>[] clazzes) {
        // ������ͬ
        if (types.length != clazzes.length) {
            return false;
        }

        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(clazzes[i]).equals(types[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * <p>
     * ��ȡ�����Ĳ�����
     * </p>
     * 
     * @param m
     * @return
     */
    public static List<String> getMethodParamNames(final Method m) {
    	final List<String> paramNames = new ArrayList<String>();
        ClassReader cr = null;
        try {
        	InputStream in = m.getDeclaringClass().getResourceAsStream("/" + m.getDeclaringClass().getName().replace('.', '/') + ".class");
            cr = new ClassReader(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        cr.accept(new ClassVisitor(Opcodes.ASM4) {
            @Override
            public MethodVisitor visitMethod(final int access,
                    final String name, final String desc,
                    final String signature, final String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(desc);
                // ��������ͬ���Ҳ���������ͬ
                if (!name.equals(m.getName())
                        || !sameType(args, m.getParameterTypes())) {
                    return super.visitMethod(access, name, desc, signature,
                            exceptions);
                }
                MethodVisitor v = super.visitMethod(access, name, desc,
                        signature, exceptions);
                return new MethodVisitor(Opcodes.ASM4, v) {
                	// ���Ƿ��ʷ����еı������������������������ڲ��ı���
                    @Override
                    public void visitLocalVariable(String name, String desc,
                            String signature, Label start, Label end, int index) {
                    	if(m.getParameterTypes().length>0){
                    		paramNames.add(name);
                    	}
                        super.visitLocalVariable(name, desc, signature, start,
                                end, index);
                    }

                };
            }
        }, 0);
        paramNames.remove("this");
        int length = m.getParameterTypes().length;
        if(length>0&&paramNames.size()>0){
        	Iterator<String> iterator = paramNames.iterator();
        	int i=1;
        	while(iterator.hasNext()){
        		iterator.next();
        		if(i>length){
        			iterator.remove();
        		}
        		i++;
        	}
        }
        		
        return paramNames;
    }
}
