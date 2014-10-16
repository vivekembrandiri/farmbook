package com.utilitiespackage;

import java.util.Comparator;

public class SortPosts implements Comparator<String[]>{
	
	private final String ASCENDING="a";
	private String type;
	private int colno;
	
	public SortPosts(String type,int colno) {
		this.type=type;
		this.colno = colno;
	}
	
	@Override
	public int compare(String[] lhs, String[] rhs) {

		String lts=lhs[colno],rts=rhs[colno];

		if(lts.compareTo(rts) <0)
			return (type.equals(ASCENDING) ? -1 : 1);
		else return (type.equals(ASCENDING) ? 1 : -1);
	}

}
