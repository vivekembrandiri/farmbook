package com.utilitiespackage;

import java.util.Arrays;
import java.util.Hashtable;

public class Table {
	
	public static String ASCENDING="a";
	public static String DESCENDING="d";
	
	private Hashtable<String, Integer> columnnames;
	private String[][] matrix; 
	private int no_columns;
	
	public Table(String[] cnames, int no_rows) {
		
		this.columnnames=new Hashtable<String, Integer>();
		no_columns=cnames.length;
		this.matrix = new String[no_rows][no_columns];
		
		for(int i=0; i<no_columns; i++)
			this.columnnames.put(cnames[i], i);
	}
	
	public void add(int row, String details) {
		String[] keysvalues = details.split("\\@");
		String[] temparray = new String[no_columns];
		
		for(int i=0; i<no_columns; i++) {
			String key = keysvalues[i].split("\\:")[0];
			String value = "";
			
			if(key.equals("timestamp") || key.equals("description") || key.equals("text"))
				value=keysvalues[i].substring(keysvalues[i].indexOf(':')+1);
			else
				value = (keysvalues[i].split("\\:").length==2 ? keysvalues[i].split("\\:")[1] : "");
			temparray[columnnames.get(key)] = value;
		}
		
		matrix[row]=temparray;
	}
	
	public String get(int index,String cname) {
		
		return matrix[index][columnnames.get(cname)];
	}
	
	public String show(int index) {
		
		String result="";
		
		for(int i=0; i<no_columns; i++)
			result=result+matrix[index][i]+"; ";
		
		return result;
	}
	
	public void sort(String type) {
		Arrays.sort(matrix,new SortPosts(type,columnnames.get("timestamp")));
	}
}
