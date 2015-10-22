package core.util;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProcessingDetector {
	final public static Class<?> pappletClass = getPappletClass();
	private static Object pappletObject = null;

	public static boolean inProcessing() {
		return pappletClass != null;
	}

	private static Class<?> getPappletClass() {
		try {
			return Class.forName("processing.core.PApplet");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
	
	public static void setPappletObject(Object o) {
		if (pappletClass != null && pappletClass.isAssignableFrom(o.getClass())) {
			pappletObject = o;
		}
		// TODO: add error checking here...
	}
	
	public static Object getPapplet() {
		return pappletObject;
	}
	
	public static String sketchPath(String file) {
		if (inProcessing() && pappletObject != null) {
			try {
				Method m = pappletClass.getMethod("sketchPath", String.class);
				m.setAccessible(true);
				return (String)m.invoke(pappletObject, file);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String getProcessingSketchClassName() {
		StackTraceElement[] es = Thread.currentThread().getStackTrace();
		  String appletName = null;
		  for (int i = 0; i < es.length; i++) {
		     if (es[i].getClassName().equals("processing.core.PApplet") && i > 0) {
		       appletName = es[i-1].getClassName();
		       break;
		     }
		  }
		  return appletName;
	}
	
	public static String tryToFixPath(String path) {
		if (!inProcessing() || URLPrepper.isURL(path)) return path;
		
		File f = new File(path);
		if (!f.exists() && !f.isAbsolute()) {
			String try1 = tryToFixPath(sketchPath(path));
			if (try1.equals(path)) // didn't do anything
				return tryToFixPath(sketchPath("data/" + path));
			else 
				return try1;
		}
		
		return path;
	}
}
