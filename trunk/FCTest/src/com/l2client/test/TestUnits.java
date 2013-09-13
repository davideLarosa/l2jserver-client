package com.l2client.test;

import com.l2client.model.l2j.ServerValues;

public class TestUnits {

	public static void main(String[] args){
		System.out.println(ServerValues.getServerString(-9933.133f, 25.606129f, 8348.487f));
		System.out.println(ServerValues.getServerString(-9943.955f, 20.350714f, 8310.191f));
		System.out.println(ServerValues.getServerString(-9920.653f, 17.862331f, 8280.138f));
		System.out.println(ServerValues.getServerString(-9895.944f, 16.703045f, 8292.451f));
		System.out.println(ServerValues.getServerString(-9852.93f, 14.763317f, 8227.26f));
		System.out.println(ServerValues.getServerString(-9846.71f, 15.108395f, 8258.238f));
		
		System.out.println(ServerValues.getServerString(-4458.625f,-194f,16141.9375f));
	}
}
