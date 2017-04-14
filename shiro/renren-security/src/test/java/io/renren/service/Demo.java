package io.renren.service;

<<<<<<< HEAD
import io.renren.controller.SysPageController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
=======
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
>>>>>>> 0aa825e1bc50db578f66393f098cead07819e738

import org.junit.Test;

public class Demo {

	@Test
<<<<<<< HEAD
	public void test(){
	    String [] a ={"1","2"};
	    List b=Arrays.asList(a);
	    a[0]="0";
		System.out.println(b);
		
	}
=======
	public void t() {

		File f=new File("D:\\hh.txt");
		//使用默认编码        
        InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int len;
        try {
			while ((len = reader.read()) != -1) {
			    System.out.print((char) len);//爱生活，爱Android

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			reader.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

         //指定编码 
        InputStreamReader reader1 = null;
		try {
			reader1 = new InputStreamReader(new FileInputStream(f),"utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        int len1;
        try {
			while ((len1 = reader1.read()) != -1) {
			    System.out.print((char) len1);//????????Android
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			reader1.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public boolean getV(char c){
		System.out.println(c);
		return true;
	}
	
	
	

	class Person {

		private String name;

		private int age;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
		
		

	}
	
	
	public void swap(Person p){
		p.setName("444");
		Person p1=new Person();
		p1.setName("333");
		p=p1;
		
		
	}
	
	public void swap2(int a,int b){
		a=2;
		b=3;
		System.out.println(a);
		System.out.println(b);
		
	}

>>>>>>> 0aa825e1bc50db578f66393f098cead07819e738
}
