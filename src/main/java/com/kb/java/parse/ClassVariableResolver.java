package com.kb.java.parse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassVariableResolver {

	private Map<String, String> varVsTypeMap = new HashMap<String, String>();
	private Map<String, String> typeMap;
	private String pkg;
	private static Set<String> langClassSet = new HashSet<String>();
	private Set<TypeLocation> typeLocations = new HashSet<TypeLocation>(); 
	
	static{
		String javaLangClasses = "Appendable,CharSequence,Comparable,Readable,Thread.UncaughtExceptionHandler,"
				+ "AutoCloseable,Cloneable,Iterable,Runnable,Boolean,Character,Character.UnicodeBlock,ClassLoader,Compiler,Enum,"
				+ "InheritableThreadLocal,Long,Number,Package,ProcessBuilder,Runtime,SecurityManager,StackTraceElement,"
				+ "String,StringBuilder,Thread,ThreadLocal,Void,Byte,Character.Subset,Class,ClassValue,Double,Float,Integer,"
				+ "Math,Object,Process,ProcessBuilder.Redirect,RuntimePermission,Short,StrictMath,StringBuffer,System,ThreadGroup,"
				+ "Throwable,Character.UnicodeScript,Thread.State,ProcessBuilder.Redirect.Type,ArithmeticException,ArrayStoreException,"
				+ "ClassNotFoundException,EnumConstantNotPresentException,IllegalAccessException,IllegalMonitorStateException,"
				+ "IllegalThreadStateException,InstantiationException,NegativeArraySizeException,NoSuchMethodException,"
				+ "NumberFormatException,RuntimeException,StringIndexOutOfBoundsException,"
				+ "UnsupportedOperationException,ArrayIndexOutOfBoundsException,ClassCastException,"
				+ "CloneNotSupportedException,Exception,IllegalArgumentException,IllegalStateException,IndexOutOfBoundsException,"
				+ "InterruptedException,NoSuchFieldException,NullPointerException,ReflectiveOperationException,SecurityException,"
				+ "TypeNotPresentException,AbstractMethodError,BootstrapMethodError,ClassFormatError,ExceptionInInitializerError,"
				+ "IncompatibleClassChangeError,InternalError,NoClassDefFoundError,NoSuchMethodError,StackOverflowError,"
				+ "UnknownError,UnsupportedClassVersionError,VirtualMachineError,AssertionError,ClassCircularityError,"
				+ "Error,IllegalAccessError,InstantiationError,LinkageError,NoSuchFieldError,OutOfMemoryError,"
				+ "ThreadDeath,UnsatisfiedLinkError,VerifyError,Deprecated,SafeVarargs,Override,SuppressWarnings";
		String[] langTypes = javaLangClasses.split(",");
		for(String t: langTypes){
			langClassSet.add(t);
		}
		
	}
	
	public ClassVariableResolver(Map<String, String> typeMap, String pkg) {
		this.typeMap = typeMap;
		this.pkg = pkg;
	}

	public void addType(String shortType, String longType) {
		typeMap.put(shortType, longType);
	}

	public void addVarType(String var, String type, Integer line, Integer column) {
		String longType = typeMap.get(type);
		if (longType == null) {
			// Use short type
			longType = type;
		}
		varVsTypeMap.put(var, longType);
		typeLocations.add(new TypeLocation(longType, line, column));
	}

	public String getType(String shortType) {
		return typeMap.get(shortType);
	}

	public String getSafeType(String shortType) {
		String type = getType(shortType);
		
		if (type == null) {
			//May be because it is a java.lang type
			if(langClassSet.contains(shortType)){
				type = "java.lang."+shortType;
			}
		}
		
		if(type == null){
			type = (pkg==null? "": pkg+".") + shortType;
		}
		
		return type;
	}

	public String getVarType(String var) {
		String type = varVsTypeMap.get(var);
		if (type == null) {
			// probably a static call.
			type = "<Static>" + getSafeType(var);
		}
		return type;
	}

	public Set<TypeLocation> getTypeLocations() {
		return typeLocations;
	}


}
