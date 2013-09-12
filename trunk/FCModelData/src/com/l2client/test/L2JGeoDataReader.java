package com.l2client.test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;

public class L2JGeoDataReader {

	private static final float SCALE_HEIGHT = 8f;

	private class Point {
		public int x, y, layer;
		public short height;
		private int hash = -1;

		public String toString() {
			// first place 01 to 21, next 0000 to 2048, next 0000 to 2048, next
			// making a 10 digit number (smaller than MAX_VALUE for int)
			return String.format("%d%04d%04d", layer, x, y);
		}

		public boolean equals(Object o) {
			if (o == this)
				return true;

			if (!(o instanceof Point))
				return false;

			Point n = (Point) o;
			return (n.x == x && n.y == y && n.layer == layer);
		}

		public int hashCode() {
			if (hash < 0)
				hash = Integer.valueOf(toString());

			return hash;
		}
	}

	private HashMap<Integer, Point> pointMap;
	private int layer0Cells = 0;
	private byte maxLayers = 0;
	
	private float minH =0f;
	private float maxH =0f;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		L2JGeoDataReader reader = new L2JGeoDataReader();
		
//		//cell indices
//		for(int y=0;y<8;y++)
//			for(int x=0;x<8;x++)
//				System.out.println("x:"+x+" y:"+y+" res:"+(((x << 3) + y) << 1)/2);
//		//from this output it is clear that cells work y*x not x*y
//		System.out.println("------------------------------------------------------");
//		//block indices
//		for(int y=0;y<8;y++)
//			for(int x=0;x<8;x++)
//				System.out.println("x:"+x+" y:"+y+" res:"+(((x << 8)) + (y)));
//		//and this shows that blocks are also in y*x order
		
		reader.readFile("21_16.l2j", 21, 16);
	}

	public L2JGeoDataReader() {
		// TODO Auto-generated constructor stub
	}

	public void readFile(String pathname, int rX, int rY) {
		try {
			pointMap = new HashMap<Integer, L2JGeoDataReader.Point>(256 * 256
					* 8 * 8 * 2);// two times the size of an empty map
			layer0Cells = 0;
			maxLayers = 0;
			DataInputStream dis = new DataInputStream(new FileInputStream(
					new File(pathname)));
			int bX = 0;
			int bY = 0;
			System.out.println("Reading blocks");
			for (bX = 0; bX < 256; bX++) {
				// System.out.print(" "+(rX * 8 * 256 + bX)+","+(rY * 8 * 256 +
				// bY));
				for (bY = 0; bY < 256; bY++) {
					// System.out.print(" "+(rX * 8 * 256 + bX)+","+(rY * 8 *
					// 256 + bY));
					int type = dis.readByte();
					switch (type) {
					case 0:
						readSimpleBlockHeight(dis, bX * 8, bY * 8);
						break;
					case 1:
						readCellBlockHeights(dis, bX * 8, bY * 8);
						break;
					default:// not really a default but case 2:
						readLayeredCellBlockHeights(dis, bX * 8, bY * 8);
						break;
					}

				}
				System.out.print(".");
				// System.out.println();
			}
			System.out.println();
			System.out.println((256 * 256 * 8 * 8)
					+ " cells loaded, hashmap contains " + pointMap.size()
					+ " entries and " + layer0Cells + " layer 0 entries, max layers was "+maxLayers);
		
			constructMesh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readLayeredCellBlockHeights(DataInputStream dis, int bX, int bY)
			throws IOException {
		//internal format y*x
		for (short x = 0; x < 8; x++)
			for (short y = 0; y < 8; y++) {
				short height = -1;
				short NSWE = 0;
				byte layers = dis.readByte();
				if (layers <= 0 || layers > 125)
					continue;

				if(layers > maxLayers)
					maxLayers = layers;
				
				if (layers > 21)
					System.out
							.println("Too many layers found in file, currently only 21 are allowed, due to hashing reasons, found "+layers+" at block "+bX+":"+bY);

				while (layers > 0) {
					height = dis.readShort();
					if (layers <= 21) {
						NSWE = height;
						height = (short) (height & 0x0fff0);
						height = (short) (height >> 1); // height / 2

						NSWE = (short) (NSWE & 0x0F);
						addCell(bX + x, bY + y, (short) -8000, layers);
					}
					layers--;
				}
			}
	}

	/**
	 * read the 8x8 cells of this block
	 * 
	 * @param dis
	 * @param bX
	 * @param bY
	 * @throws IOException
	 */
	private void readCellBlockHeights(DataInputStream dis, int bX, int bY)
			throws IOException {
		//internal format y *x
		for (short x = 0; x < 8; x++)
			for (short y = 0; y < 8; y++)
				readCellHeight(dis, bX + x, bY + y);
	}

	/**
	 * one height for all 8 cells of this block
	 * 
	 * @param dis
	 * @param bX
	 * @param bY
	 * @throws IOException
	 */
	private void readSimpleBlockHeight(DataInputStream dis, int bX, int bY)
			throws IOException {
		short height = dis.readShort();
		for (short y = 0; y < 8; y++) {
			for (short x = 0; x < 8; x++)
				addCell(bX + x, bY + y, height, 1);
			}
	}

	private void readCellHeight(DataInputStream dis, int cX, int cY)
			throws IOException {
		short height = dis.readShort();
		addCell(cX, cY, (short) -8000, 1);
	}

	private void addCell(int cX, int cY, short height, int layer) {
		// float fHeight = height * SCALE_HEIGHT;
		Point p = new Point();
		p.x = cX;
		p.y = cY;
		p.layer = layer;
		p.height = height;
		if (pointMap.containsKey(p.hashCode())) {
			Point x = pointMap.get(p.hashCode());
			System.out.println("COLLISION!! found point at:" + x
					+ " has same hashcode as " + p);
		} else
			pointMap.put(p.hashCode(), p);
		if (layer <= 1)
			layer0Cells++;
	}
	
	private void constructMesh(){
		Point p = new Point();
		p.layer = 1;
		Point[] round = new Point[8];
		float[] quads = new float[256*256*4];
		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				// 0 1 2
				// 7 c 3 center point, and 8 points around
				// 6 5 4
				int cnt = 0;
				minH =Float.MAX_VALUE;
				maxH =Float.MIN_VALUE;
				System.out.println("Constructing mesh "+i+","+j);
				for (int y = 0; y < 256; y++) {
					for (int x = 0; x < 256; x++) {
						p.hash = -1;
						p.x = (i * 256) + x;
						p.y = (j * 256) + y;
						Point center = pointMap.get(p.hashCode());
						if (center != null) {
							round = getRound(center);
							float[] quad = createQuad(center, round);
							if (quad != null) {
								quads[cnt++] = quad[0];
								quads[cnt++] = quad[1];
								quads[cnt++] = quad[2];
								quads[cnt++] = quad[3];
								
								for(int n=0;n<4;n++){
								if(quad[n]<minH)
									minH=quad[n];
								if(quad[n]>maxH)
									maxH = quad[n];
								}
							}
//						quads[cnt++]=center.height;
						}
					}
					System.out.print(".");
				}
				System.out.println();
				System.out.println("Mesh "+i+","+j+" constructed");
				System.out.println("Mesh minH:"+minH+" maxH:"+maxH);
				writeOutPutFile(quads, i, j);
			}
		}
	}

	private void writeOutPutFile(float[] quads, int a, int b) {
		FileOutputStream os;
		try {
			os = new FileOutputStream("out_"+a+"_"+b+".obj");
	
			PrintWriter p = new PrintWriter(os);

			//verts
			int i = 0;
			System.out.println("Writing file out_"+a+"_"+b+".obj");
			float h = 0f;
			for(int y = 0; y < 256; y++){
				for(int x = 0; x < 256; x++){				
//					h = quads[i]*0.00125f;
					p.printf(Locale.ENGLISH,"v %.2f %.2f %.2f\n", (float)x, quads[i++]*0.00125f, (float)y);
					p.printf(Locale.ENGLISH,"v %.2f %.2f %.2f\n", (float)x+1f, quads[i++]*0.00125f, (float)y);
					p.printf(Locale.ENGLISH,"v %.2f %.2f %.2f\n", (float)x+1f, quads[i++]*0.00125f, (float)y+1f);
					p.printf(Locale.ENGLISH,"v %.2f %.2f %.2f\n", (float)x, quads[i++]*0.00125f, (float)y+1f);
//					p.printf(Locale.ENGLISH,"%.2f ", (float)x, h, (float)y+1f);
//					i++;
				}
				p.printf("\n");
				p.flush();
				System.out.print(".");
			}
			p.flush();
			System.out.println();
			System.out.println("verts written");
			i = 1;
			for(int y = 0; y < 256; y++){
				for(int x = 0; x < 256; x++){				
					p.printf("f %d %d %d %d\n", i, i+1, i+2, i+3);
//					p.printf("f %d %d %d\n", i, i+2, i+3);
					i+=4;
				}
				p.flush();
				System.out.print(".");
			}
			p.flush();			
			System.out.println();
			p.close();
			System.out.println("faces written");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private float[] createQuad(Point center, Point[] round) {
		if(round == null && center == null)
			return new float[]{-4000f,-4000f,-4000f,-4000f};
		if(round == null)
			return new float[]{center.height, center.height, center.height, center.height};

		float[] ret = new float[]{center.height, center.height, center.height, center.height};
		//topleft is 0, 1, c, 7
		ret[0] = computMedianHeight(round[0], round[1], center, round[7]);
		//topright is 1,2,c,3
		ret[1] = computMedianHeight(round[1], round[2], center, round[3]);
		//bottomright is c,3,4,5
		ret[2] = computMedianHeight(center, round[3], round[4], round[5]);
		//bottomleft is 7,c,6,5
		ret[3] = computMedianHeight(round[7], center, round[6], round[5]);
		
		return ret;
	}

	private float computMedianHeight(Point v1, Point v2,
			Point v3, Point v4) {
		byte tot = 0;
		float f =0f;

		if(v1 != null){tot++;f+=v1.height;}
		if(v2 != null){tot++;f+=v2.height;}
		if(v3 != null){tot++;f+=v3.height;}
		if(v4 != null){tot++;f+=v4.height;}
		
		return (f/tot);
	}

	private Point[] getRound(Point center) {
		Point[] ret = new Point[8];
		Point p = new Point();
		p.layer = 1;
		if(center != null){
//			0 1 2
//			7 c 3	8 points around center
//			6 5 4
			if(center.x>0 && center.y>0){
			p.hash = -1;
			p.x = center.x-1;
			p.y = center.y-1;
			ret[0] = pointMap.get(p.hashCode());
			}
			if(center.y>0){
			p.hash = -1;
			p.x = center.x;
			p.y = center.y-1;
			ret[1] = pointMap.get(p.hashCode());
			}
			if(center.y>0){
			p.hash = -1;
			p.x = center.x+1;
			p.y = center.y-1;
			ret[2] = pointMap.get(p.hashCode());
			}
			p.hash = -1;
			p.x = center.x+1;
			p.y = center.y;
			ret[3] = pointMap.get(p.hashCode());
			p.hash = -1;
			p.x = center.x+1;
			p.y = center.y+1;
			ret[4] = pointMap.get(p.hashCode());
			p.hash = -1;
			p.x = center.x;
			p.y = center.y+1;
			ret[5] = pointMap.get(p.hashCode());
			if(center.x>0){
			p.hash = -1;
			p.x = center.x-1;
			p.y = center.y+1;
			ret[6] = pointMap.get(p.hashCode());
			}
			if(center.x>0){
			p.hash = -1;
			p.x = center.x-1;
			p.y = center.y;
			ret[7] = pointMap.get(p.hashCode());
			}
			
		}
		return ret;
	}

}
