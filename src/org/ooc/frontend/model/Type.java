package org.ooc.frontend.model;

import java.io.IOException;
import java.util.List;

import org.ooc.frontend.Visitor;
import org.ooc.frontend.model.interfaces.MustBeResolved;
import org.ooc.frontend.model.tokens.Token;
import org.ooc.middle.OocCompilationError;
import org.ooc.middle.hobgoblins.Resolver;

public class Type extends Node implements MustBeResolved {

	public static class Classification {
		public static final int POINTER = 1;
		public static final int NUMBER = 2;
		public static final int CLASS = 4;
	}

	protected String name;
	protected int pointerLevel;
	protected int referenceLevel;
	protected Declaration ref;
	protected boolean isArray;
	protected List<Type> typeParams;
	
	public Type(String name, Token startToken) {
		this(name, 0, startToken);
	}
	
	public Type(String name, int pointerLevel, Token startToken) {
		this(name, pointerLevel, 0, startToken);
	}
	
	public Type(String name, int pointerLevel, int referenceLevel, Token startToken) {
		super(startToken);
		this.name = name;
		this.pointerLevel = pointerLevel;
		this.referenceLevel = referenceLevel;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPointerLevel(int pointerLevel) {
		this.pointerLevel = pointerLevel;
	}

	public int getPointerLevel() {
		return pointerLevel;
	}
	
	public void setReferenceLevel(int referenceLevel) {
		this.referenceLevel = referenceLevel;
	}
	
	public int getReferenceLevel() {
		return referenceLevel;
	}
	
	public Declaration getRef() {
		return ref;
	}
	
	public void setRef(Declaration ref) {
		this.ref = ref;
	}

	@Override
	public void accept(Visitor visitor) throws IOException {
		visitor.visit(this);
	}
	
	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public void acceptChildren(Visitor visitor) throws IOException {}
	
	@Override
	public String toString() {
		
		if(pointerLevel == 0 && referenceLevel == 0) {
			return name;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		for(int i = 0; i < pointerLevel; i++) {
			if(isArray) sb.append("[]");
			else sb.append('*');
		}
		for(int i = 0; i < referenceLevel; i++) {
			sb.append('@');
		}
		return sb.toString();
		
	}
	
	public String getMangledName() {
		
		if(pointerLevel == 0) {
			return name;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		for(int i = 0; i < pointerLevel + referenceLevel; i++) {
			sb.append("__star");
		}
		return sb.toString();
		
	}

	public boolean isVoid() {
		return (name.equals("void") || name.equals("Void")) && isFlat();
	}

	public boolean isFlat() {
		return pointerLevel == 0 && !(ref instanceof ClassDecl);
	}
	
	@Override
	public boolean replace(Node oldie, Node kiddo) {
		if(oldie == ref) {
			ref = (Declaration) kiddo;
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Type) {
			Type type = (Type) obj;
			return name.equals(type.name) && pointerLevel == type.getPointerLevel();
		}
		return super.equals(obj);
	}
	
	public boolean resolve(NodeList<Node> stack, Resolver res, boolean fatal) throws IOException {

		ref = stack.getModule().getType(name);

		if(ref == null && name.equals("This")) {
			int index = stack.find(TypeDecl.class);
			if(index == -1) {
				throw new OocCompilationError(this, stack, "Using 'This' outside a type definition. Wtf? stack = "+stack);
			}
			TypeDecl typeDecl = (TypeDecl) stack.get(index);
			name = typeDecl.getName();
			ref = typeDecl;
			return false;
		}
		
		if(ref == null) {
			int genIndex = stack.find(Generic.class);
			if(genIndex != -1) {
				Generic gen = (Generic) stack.get(genIndex);
				List<TypeParam> params = gen.getTypeParams();
				for(TypeParam param: params) {
					if(param.name.equals(name)) {
						ref = param;
						return false;
					}
				}
			}
		}
		
		if(ref == null && fatal) {
			throw new OocCompilationError(this, stack, "Couldn't resolve type "+getName());
		}
		
		return ref == null;
		
	}
	
	@Override
	public boolean isResolved() {
		return ref != null;
	}
	
	public void setArray(boolean isArray) {
		this.isArray = isArray;
	}
	
	public boolean isArray() {
		return isArray;
	}
	
	public Type getGroundType() {
		return getGroundType(null);
	}
	
	public Type getGroundType(Resolver res) {
		if(ref instanceof CoverDecl) {
			CoverDecl coverDecl = (CoverDecl) ref;
			if(coverDecl.getFromType() != null) {
				Type rawType = coverDecl.getFromType().getGroundType(res);
				Type groundType = new Type(rawType.name, pointerLevel, referenceLevel, rawType.startToken);
				if(res == null) {
					groundType.ref = ref;
				} else {
					groundType.resolve(res);
				}
				return groundType;
			}
		}
		return this;
	}
	
	public Type getFlatType(Resolver res) {
		Type returnType = this;
		while(returnType.ref instanceof CoverDecl) {
			CoverDecl coverDecl = (CoverDecl) returnType.ref;
			Type fromType = coverDecl.getFromType();
			if(fromType == null) break;
			if(fromType.referenceLevel <= 0) break;
			
			returnType = new Type(fromType.name, fromType.pointerLevel - 1,
					returnType.referenceLevel - 1, fromType.startToken);
			returnType.resolve(res);
		}
		
		return returnType;
	}

	public boolean fitsIn(Type innerType) {
		if (equals(innerType)) return true;
		if (getClassification() == innerType.getClassification()) return true;
		return false;
	}
	
	public int getClassification() {
		if(!isFlat()) return Classification.POINTER;
		
		if(name.equals("Int")   || name.equals("UInt")  || name.equals("Short")
		|| name.equals("UShort")|| name.equals("Long")  || name.equals("ULong")
		|| name.equals("LLong") || name.equals("ULLong")|| name.equals("Char")
		|| name.equals("UChar") || name.equals("Int8")  || name.equals("Int16")
		|| name.equals("Int32") || name.equals("Int64") || name.equals("UInt8")
		|| name.equals("UInt16")|| name.equals("UInt32")|| name.equals("UInt64")
		|| name.equals("SizeT")
		) return Classification.NUMBER;
		
		return Classification.CLASS;
	}

	public void resolve(Resolver res) {
		ref = res.module.getType(name);
	}
	
}

