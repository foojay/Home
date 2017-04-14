package io.renren.service;

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

import org.junit.Test;

public class Demo {

	@Test
	public void test(){
	    String [] a ={"1","2"};
	    List b=Arrays.asList(a);
	    a[0]="0";
		System.out.println(b);
		
	}
}
