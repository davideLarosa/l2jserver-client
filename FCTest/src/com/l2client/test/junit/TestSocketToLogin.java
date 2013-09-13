package com.l2client.test.junit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import junit.framework.TestCase;



public class TestSocketToLogin extends TestCase {

	protected Socket s;

	public void testSelf() {
		String host = "localhost";

		try {
			InetAddress i = InetAddress.getByName(host);
			InetAddress j = InetAddress.getLocalHost();
			if("localhost".equals(host)|| "127.0.0.1".equals(host)) {
				s = new Socket(j,2106);
			}
			else {
				s = new Socket(i, 2106);
			}
			if(!s.isConnected())
				return;
			byte[] buffer = new byte[1024];
			final OutputStream out = s.getOutputStream();
			final InputStream in = s.getInputStream();
			InetAddress rS = s.getInetAddress();
			InetAddress lS = s.getLocalAddress();
			if(lS instanceof Inet4Address)
				System.out.println("lS: Ipv4");
			if(lS instanceof Inet6Address)
				System.out.println("lS: Ipv6");
			if(rS instanceof Inet4Address)
				System.out.println("rS: Ipv4");
			if(rS instanceof Inet6Address)
				System.out.println("rS: Ipv6");
			int read = 0;
//			out.write(0);
			while(read >= 0 || !s.isConnected()){
				read = in.read(buffer);
				System.out.println("Read "+read+" bytes from server");
			}
			in.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
