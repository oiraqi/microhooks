package io.microhooks.instrumentation;

import net.bytebuddy.dynamic.ClassFileLocator;

public class Loader extends ClassLoader {
        private ClassFileLocator classFileLocator;

        public Loader(ClassFileLocator classFileLocator) {
            this.classFileLocator = classFileLocator;
        }

        public Class<?> findClass(String name) {
            byte[] bytes;
            try {
                bytes = classFileLocator.locate(name).resolve();
                return defineClass(name, bytes, 0, bytes.length);
            } catch (Exception e) {
                if (!name.equals("io.microhooks.containers.spring.Config"))
                    e.printStackTrace();
            }
            return null;
        }
    }
