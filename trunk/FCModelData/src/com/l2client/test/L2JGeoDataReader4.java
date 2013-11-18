package com.l2client.test;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Reading of l2j geodata files (*.lj2 ending) and generating wavefront object files 
 * currently pretty dumb
 * requires [name].l2j files to be present in project root
 * creates [name] directory and places below this one 8x8 directories. for each tile to place one containig the .obj
 * file. 
 *
 */
public class L2JGeoDataReader4 {

	/**
	 * On true create tiles with top left at 0/0 otherwise at world position
	 */
	private static boolean relative = true;
	private static boolean removeRemote = true;
	private static boolean smoothLevel0 = true;
	private static final float SCALE_HEIGHT = 16f;
	private static int MAX_D = 64;
	private static final byte NSWE_EAST = 1;
	private static final byte NSWE_WEST = 2;
	private static final byte NSWE_SOUTH = 4;
	private static final byte NSWE_NORTH = 8;
	private static final byte NSWE_ALL = 15;
	private static float[] NO_CELL = new float[]{-8000f, -8000f, -8000f, -8000f};//{Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};

	private class Point {
		public short x, y, layers;
		public short[] heights;
		public byte[] NWSE;
		public boolean corrupHeights = false;

		/**
		 * 
		 */
		public Point(int i, int j)
		{
			x = (short) i;
			y = (short) j;
		}

		public String toString() {
			// first place 1 , next 0000 to 2048, next 0000 to 2048, next
			// making a 9 digit number (smaller than MAX_VALUE for int)
			return String.format("1%04d%04d", x, y);
		}

	}

	private static IntBuffer indexs;

	private static MappedByteBuffer geo;

//	private HashMap<Integer, Point> pointMap;
	private int layer0Cells = 0;
	private byte maxLayers = 0;
	private int minDistance = Short.MAX_VALUE;
	
	private Point[][] pointArray;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		File f = new File(".");
		FilenameFilter fil = new FilenameFilter()
		{
			
			@Override
			public boolean accept(File dir, String name)
			{
				if (name.endsWith(".l2j"))
					return true;

				return false;
			}
		};
		
		for(File ff : f.listFiles(fil)){
		
		L2JGeoDataReader4 reader = new L2JGeoDataReader4();
		
		String[] split = ff.getName().substring(0,ff.getName().length()-4).split("_");
		int x = Integer.valueOf(split[0]);
		int y = Integer.valueOf(split[1]);
////		//cell indices
////		for(int y=0;y<8;y++)
////			for(int x=0;x<8;x++)
////				System.out.println("x:"+x+" y:"+y+" res:"+(((x << 3) + y) << 1)/2);
////		//from this output it is clear that cells work y*x not x*y
////		System.out.println("------------------------------------------------------");
////		//block indices
////		for(int y=0;y<8;y++)
////			for(int x=0;x<8;x++)
////				System.out.println("x:"+x+" y:"+y+" res:"+(((x << 8)) + (y)));
////		//and this shows that blocks are also in y*x order
//		int x=21;
//		int y=16;
//		reader.readFile(x+"_"+y+".l2j", x, y);
//		reader.constructMesh(x,y);
		
		reader.readFile(ff.getAbsolutePath(), x, y);
		reader.constructMesh(x,y);
////		System.out.println("------------------------------------------------------");
////		reader.writeOrgGeo();
////		System.out.println("------------------------------------------------------");
////		reader.writeOwnGeo();
////		System.out.println("------------------------------------------------------");
		}
	}


	public L2JGeoDataReader4() {
	}

	public void readFile(String pathname, int rX, int rY) {
		try {
//			pointMap = new HashMap<Integer, L2JGeoDataReader2.Point>(256 * 256
//					* 8 * 8 * 2);// two times the size of an empty map
			pointArray = new Point[256*8][256*8];
			layer0Cells = 0;
			maxLayers = 0;
			DataInputStream dis = new DataInputStream(new FileInputStream(
					new File(pathname)));
			
			int bX = 0;
			int bY = 0;
			System.out.println("Reading blocks");
			for (bX = 0; bX < 256; bX++) {
				for (bY = 0; bY < 256; bY++) {
					byte type = dis.readByte();
					switch (type) {
					case 0:
//						System.out.println("0");
						readSimpleBlockHeight(dis, bX * 8, bY * 8);
						break;
					case 1:
//						System.out.println("1");
						readCellBlockHeights(dis, bX * 8, bY * 8);
						break;
					default:// not really a default but case 2:
//						System.out.println("2");
						readLayeredCellBlockHeights(dis, bX * 8, bY * 8);
						break;
					}

				}
				System.out.print(".");
				// System.out.println();
			}
			System.out.println();
			System.out.println((256 * 256 * 8 * 8)
					+ " cells loaded, min distance between valid layers was "+minDistance); //, hashmap contains " + pointMap.size()
//					+ " entries and " + layer0Cells + " layer 0 entries, max layers was "+maxLayers);
			System.out.println();
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

				Point pt = new Point(bX + x, bY + y);
				pt.layers = layers;
				pt.heights = new short[layers];
				pt.NWSE = new byte[layers];

				while (layers > 0) {
					height = readShort(dis);
						NSWE = height;
						height = (short) (height & 0x0fff0);
						height = (short) (height >> 1); // height / 2

						NSWE = (short) (NSWE & 0x0F);
					layers--;
					pt.heights[layers] = height;
					pt.NWSE[layers] = (byte) NSWE;
				}
				boolean corruptHeights = false;
				int dist = minDistance;
				for(int i=1;i<pt.layers;i++){
					int d = Math.abs(pt.heights[i]-pt.heights[i-1]);
					if(d <= 0){
//						System.out.println();
						System.out.println("Corrupt layers at "+pt.x+","+pt.y+" leading to 0 distance between layers @"+i+" and "+(i-1));
//						System.out.println();
						corruptHeights = true;
						pt.corrupHeights = corruptHeights;
						break;
					}
					if(d<dist)
						dist = d;
				}
				if(!corruptHeights && dist<minDistance)
					minDistance = dist;
//				pointMap.put(pt.hashCode(), pt);
				pointArray[(bY+y)][(bX+x)] = pt;
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
			for (short y = 0; y < 8; y++){
				short height = readShort(dis);
				short NSWE = (short) (height & 0x0F);
				height = (short) (height & 0x0fff0);
				height = (short) (height >> 1); //height / 2
				Point pt = new Point(bX + x, bY + y);
				pt.layers = 1;
				pt.heights = new short[]{height};
				pt.NWSE = new byte[]{(byte) NSWE};
//				pointMap.put(pt.hashCode(), pt);
				pointArray[(bY+y)][(bX+x)] = pt;
			}
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
		short height = readShort(dis);
		for (short y = 0; y < 8; y++) 
			for (short x = 0; x < 8; x++){
//				addCell(bX + x, bY + y, height, 1, NSWE_ALL);
			Point pt = new Point(bX + x, bY + y);
			pt.layers = 1;
			pt.heights = new short[]{height};
			pt.NWSE = new byte[]{NSWE_ALL};
//			pointMap.put(pt.hashCode(), pt);
//			pointArray[((bY+y)*2048)+(bX+x)] = pt;
			pointArray[(bY+y)][(bX+x)] = pt;
			}
	}

	
	private void constructMesh(int a, int b){

		Point[] round = new Point[8];
		float[] quads = new float[256*256*4];
		ArrayList<Float> uQuads = new ArrayList<Float>(1024);

		for (int j = 0; j < 8; j++) {
			for (int i = 0; i < 8; i++) {
				// 0 1 2
				// 7 c 3 center point, and 8 points around
				// 6 5 4
				int cnt = 0;
				int uCnt = 0;
				uQuads.clear();
//				System.out.println("Constructing mesh "+i+","+j);
				for (int y = 0; y < 256; y++) {
					for (int x = 0; x < 256; x++) {
//						Point center = pointMap.get(hashOf(((i * 256) + x), ((j * 256) + y)));
						Point center = pointArray[(j * 256) + y][(i * 256) + x];//(y*2048+j)+(x*256)+i];  //(bY*2048)+y+(bX+x)]; 
//						if(center != c2)
//							System.out.println("c2 != center");
						
						if (center != null) {
							round = getRound(center);
							float[] quad = createQuad(center, round, 0);
							if (quad != null) {
								quads[cnt++] = quad[0];
								quads[cnt++] = quad[1];
								quads[cnt++] = quad[2];
								quads[cnt++] = quad[3];
							}
							
							if(center.layers > 1){
								createUpperQuads(center, uQuads, round);
							}
						}
					}
//					System.out.print(".");
				}
//				System.out.println();
//				System.out.println("Mesh "+i+","+j+" constructed");
				writeOutPutFile(quads, uQuads, i, j, a, b);
			}
		}
	}

	private void createUpperQuads(Point center, ArrayList<Float> uQuads, Point[] round)
	{
		//TODO this is not only the 8 around us, but look also at the 8 below the current layer, if all above look below, things should be fine
		for(int layer=1; layer<center.layers;layer++){
			
			float[] quad = createQuad(center, round, layer);
			if (quad != null) {
				uQuads.add((float) center.x);
				uQuads.add((float) center.y);
				uQuads.add(quad[0]);
				uQuads.add(quad[1]);
				uQuads.add(quad[2]);
				uQuads.add(quad[3]);
			}
		}
	}


	private void writeOutPutFile(float[] quads, ArrayList<Float> uQuads, int a, int b, int fx, int fy) {
		FileOutputStream os;
		int fa = fx*8;
		int fb = fy*8;
		try {
			//base dir is RegionX_RegionY/TileX_TileY
			String baseDir =fx+"_"+fy+"/"+(fa+a)+"_"+(fb+b)+"/";
			//create basedir
			File dir = new File(baseDir);
			dir.mkdirs();
			//save file as RegionX_RegionY/TileX_TileY/TileX_TileY.obj
			String fName = baseDir+(fa+a)+"_"+(fb+b)+".obj";
			os = new FileOutputStream(fName);
	
			PrintWriter p = new PrintWriter(os);

			//verts
			int i = 0;
			System.out.println("Writing file "+fName);
			float h = 0f;
			float scale = 1f/SCALE_HEIGHT;//0.00125f;
			float xd = 0f;
			float yd = 0f;
			if(!relative){
				//MUAHAHAHHA l2j's center is in x between region 19 (-32768) and 20 (+32768) and in y between region 18 (+32768) and 17 (-32768)
				//so we got to cope with this.
				xd = ((fa+a)*256)-(20*2048);//minus 20*2048, 20 because count starts with 0_0, 2048 because we do not go down to cells
				yd = (fb+b)*256-(18*2048);//minus 18*2048
			}
			p.printf(Locale.ENGLISH,"#verts\n");
			for(int y = 0; y < 256; y++){
				for(int x = 0; x < 256; x++){				
//					h = quads[i]*0.00125f;
//					i++;
//					p.printf(Locale.ENGLISH,"v %.2f %.2f %.2f\n", (float)x, h, (float)y);
//					p.printf(Locale.ENGLISH,"v %.2f %.2f %.2f\n", (float)x+1f, h, (float)y);
//					p.printf(Locale.ENGLISH,"v %.2f %.2f %.2f\n", (float)x+1f, h, (float)y+1f);
//					p.printf(Locale.ENGLISH,"v %.2f %.2f %.2f\n", (float)x, h, (float)y+1f);
					p.printf(Locale.ENGLISH,"v %.0f %.2f %.0f\n", xd+x, quads[i++]*scale, yd+y);
					p.printf(Locale.ENGLISH,"v %.0f %.2f %.0f\n", xd+x+1f, quads[i++]*scale, yd+y);
					p.printf(Locale.ENGLISH,"v %.0f %.2f %.0f\n", xd+x+1f, quads[i++]*scale, yd+y+1f);
					p.printf(Locale.ENGLISH,"v %.0f %.2f %.0f\n", xd+x, quads[i++]*scale, yd+y+1f);
//					p.printf(Locale.ENGLISH,"%.2f ", (float)x, h, (float)y+1f);

				}
//				p.printf("\n");
				p.flush();
//				System.out.print(".");
			}
			p.flush();
//			System.out.println("verts written");
			if(uQuads.size() > 0){
				p.printf(Locale.ENGLISH,"#upper verts\n");
				for(int q = 0; q<uQuads.size();){
					float x = uQuads.get(q++)-(256f*a);
					float y = uQuads.get(q++)-(256f*b);
					p.printf(Locale.ENGLISH,"v %.0f %.2f %.0f\n", xd+x, uQuads.get(q++)*scale, yd+y);
					p.printf(Locale.ENGLISH,"v %.0f %.2f %.0f\n", xd+x+1f, uQuads.get(q++)*scale, yd+y);
					p.printf(Locale.ENGLISH,"v %.0f %.2f %.0f\n", xd+x+1f, uQuads.get(q++)*scale, yd+y+1f);
					p.printf(Locale.ENGLISH,"v %.0f %.2f %.0f\n", xd+x, uQuads.get(q++)*scale, yd+y+1f);
				}
//				System.out.println("upper verts written");
			}
//			i = 1;
//			p.printf(Locale.ENGLISH,"#texture\n");
//			float uvScale = 0.25f;
//			float step = 1f/256f*uvScale;
//			for(int y = 0; y < 256; y++){
//				for(int x = 0; x < 256; x++){				
//					p.printf(Locale.ENGLISH,"vt %.4f %.4f\n", step*x, step*y);
//					p.printf(Locale.ENGLISH,"vt %.4f %.4f\n", step*(x+1), step*y);
//					p.printf(Locale.ENGLISH,"vt %.4f %.4f\n", step*(x+1), step*(y+1));
//					p.printf(Locale.ENGLISH,"vt %.4f %.4f\n", step*x, step*(y+1));
//				}
//				p.flush();
//			}
//			p.flush();
////			System.out.println("verts written");
//			if(uQuads.size() > 0){
//				p.printf(Locale.ENGLISH,"#upper verts\n");
//				for(int q = 0; q<uQuads.size();){
//					float x = uQuads.get(q++)-(256f*a);
//					float y = uQuads.get(q++)-(256f*b);
//					p.printf(Locale.ENGLISH,"vt %.4f %.4f\n", step*x, step*y);
//					p.printf(Locale.ENGLISH,"vt %.4f %.4f\n", step*(x+1), step*y);
//					p.printf(Locale.ENGLISH,"vt %.4f %.4f\n", step*(x+1), step*(y+1));
//					p.printf(Locale.ENGLISH,"vt %.4f %.4f\n", step*x, step*(y+1));
//				}
////				System.out.println("upper verts written");
//			}
			i = 1;
			p.printf(Locale.ENGLISH,"#faces\n");
			for(int y = 0; y < 256; y++){
				for(int x = 0; x < 256; x++){				
					p.printf("f %d %d %d %d\n", i+3, i+2, i+1, i);
//					p.printf("f %d %d %d\n", i, i+2, i+3);
					i+=4;
				}
				p.flush();
//				System.out.print(".");
			}
			p.flush();
//			System.out.println();
//			System.out.println("faces written");
			if(uQuads.size() > 0){
				p.printf(Locale.ENGLISH,"#upper faces\n");
				for(int q = uQuads.size()/6;q>0;q-- ){
					p.printf("f %d %d %d %d\n", i+3, i+2, i+1, i);
					i+=4;
				}
//				System.out.println("uFaces written");
			}
			p.flush();			
			p.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private float[] createQuad(Point center, Point[] round, int layer) {
		if(round == null && center == null)
			return NO_CELL;
		
		if(removeRemote)
			if(((center.NWSE[layer] & NSWE_NORTH) == 0) && 
				((center.NWSE[layer] & NSWE_EAST) == 0) &&
				((center.NWSE[layer] & NSWE_SOUTH) == 0) &&
				((center.NWSE[layer] & NSWE_WEST) == 0))
				return NO_CELL;
		
		if(layer >= center.layers){
			System.out.println("ERROR, higher layer ["+(layer+1)+"] than available ["+center.layers+"]");
			return NO_CELL;
		}

		if(round == null)
			return new float[]{center.heights[layer], center.heights[layer], center.heights[layer], center.heights[layer]};

		// 0 1 2
		// 7 c 3 center point, and 8 points around
		// 6 5 4
		float[] ret = new float[]{center.heights[0], center.heights[0], center.heights[0], center.heights[0]};	
		Point [] wa = new Point[4];
		wa[0] = center;
		//topleft is 0, 1, c, 7
		wa[1]=round[0];wa[2]=round[1];wa[3]=round[7];
		if(smoothLevel0 && layer == 0){
			ret[0] = computMedianHeight(wa, layer);
//			ret[0] = computMedianHeight2(wa, layer);
		} else {
			if((center.NWSE[layer] & NSWE_NORTH) == 0)
				wa[2]=null;
			if((center.NWSE[layer] & NSWE_WEST) == 0)
				wa[3]=null;
			ret[0] = computMedianHeight2(wa, layer);
		}
		

		//topright is 1,2,c,3
		wa[1]=round[1];wa[2]=round[2];wa[3]=round[3];
		if(smoothLevel0 && layer == 0){
			ret[1] = computMedianHeight(wa, layer);
//			ret[1] = computMedianHeight2(wa, layer);
		} else {
			if((center.NWSE[layer] & NSWE_NORTH) == 0)
				wa[1]=null;
			if((center.NWSE[layer] & NSWE_EAST) == 0)
				wa[3]=null;
			ret[1] = computMedianHeight2(wa, layer);
		}
		
		
		//bottomright is c,3,4,5
		wa[1]=round[3];wa[2]=round[4];wa[3]=round[5];
		if(smoothLevel0 && layer == 0){
			ret[2] = computMedianHeight(wa, layer);
//			ret[2] = computMedianHeight2(wa, layer);
		} else {
			if((center.NWSE[layer] & NSWE_SOUTH) == 0)
				wa[3]=null;
			if((center.NWSE[layer] & NSWE_EAST) == 0)
				wa[1]=null;
			ret[2] = computMedianHeight2(wa, layer);
		}
		
		
		//bottomleft is 7,c,6,5
		wa[1]=round[7];wa[2]=round[6];wa[3]=round[5];
		if(smoothLevel0 && layer == 0){
			ret[3] = computMedianHeight(wa, layer);
//			ret[3] = computMedianHeight2(wa, layer);
		} else {
			if((center.NWSE[layer] & NSWE_SOUTH) == 0)
				wa[3]=null;
			if((center.NWSE[layer] & NSWE_WEST) == 0)
				wa[1]=null;
			ret[3] = computMedianHeight2(wa, layer);
		}
		
		
		return ret;
	}
	
	private float computMedianHeight(Point [] cells, int layer) {
		byte tot = 0;
		float f =0f;
	
		for (int i = 0; i < 4; i++){
			if (cells[i] != null && (layer < cells[i].layers)){
				tot++;
				f += cells[i].heights[layer];
			}
		}

		return (f/tot);
		/*	private float computMedianHeight(Point v1, Point v2,
			Point v3, Point v4, int layer) {
		byte tot = 0;
		float f =0f;

		if(v1 != null && (layer < v1.layers)){tot++;f+=v1.heights[layer];}
		if(v2 != null && (layer < v2.layers)){tot++;f+=v2.heights[layer];}
		if(v3 != null && (layer < v3.layers)){tot++;f+=v3.heights[layer];}
		if(v4 != null && (layer < v4.layers)){tot++;f+=v4.heights[layer];}
		
		return (f/tot);*/
	}

	private float computMedianHeight2(Point [] cells, int layer){
		byte tot = 1;
		short h = cells[0].heights[layer];
		float f = h;
		int v = 0;
		
		for (int i = 1; i < 4; i++){
			if (cells[i] != null && (layer < cells[i].layers)){
				v = getNearestBelow(h, cells[i]);
				if (v != Integer.MIN_VALUE)	{
					tot++;
					f += v;
				}
			}
		}
		
		return (f/tot);
	}
	
	private int getNearestBelow(short height, Point cell){
		int ret = Integer.MIN_VALUE;
		int lastD = Integer.MAX_VALUE;
		if(cell != null){
			for(int i= 0;i<cell.layers;i++){
				int d = Math.abs(cell.heights[i]-height);
				if(d<MAX_D && d<lastD){
					lastD=d;
					ret = cell.heights[i];
				}
			}
		}
		return ret;
	}

	private Point[] getRound(Point center) {
		Point[] ret = new Point[8];
		if(center != null){
//			0 1 2
//			7 c 3	8 points around center
//			6 5 4
			//pointArray[(j * 256) + y][(i * 256) + x]
			if(center.x>0 && center.y>0){
//			ret[0] = pointMap.get(hashOf(center.x-1, center.y-1));
			ret[0] = pointArray[center.y-1][center.x-1];
			}
			if(center.y>0){
//			ret[1] = pointMap.get(hashOf(center.x, center.y-1));
			ret[1] = pointArray[center.y-1][center.x];
			}
			if(center.y>0 && center.x < 2047){
//			ret[2] = pointMap.get(hashOf(center.x+1, center.y-1));
			ret[2] = pointArray[center.y-1][center.x+1];
			}
//			ret[3] = pointMap.get(hashOf(center.x+1, center.y));
			if(center.x <2047)
				ret[3] = pointArray[center.y][center.x+1];
//			ret[4] = pointMap.get(hashOf(center.x+1, center.y+1));
			if(center.x <2047 && center.y < 2047)
				ret[4] = pointArray[center.y+1][center.x+1];
//			ret[5] = pointMap.get(hashOf(center.x, center.y+1));
			if(center.y <2047)
				ret[5] = pointArray[center.y+1][center.x];
			if(center.x>0 && center.y <2047){
//			ret[6] = pointMap.get(hashOf(center.x-1, center.y+1));
			ret[6] = pointArray[center.y+1][center.x-1];
			}
			if(center.x>0){
//			ret[7] = pointMap.get(hashOf(center.x-1, center.y));
			ret[7] = pointArray[center.y][center.x-1];
			}
			
		}
		return ret;
	}

	private short readShort(DataInputStream is)
	    throws IOException,
	           EOFException
	  {
	    final int low = is.readByte() & 0xff;
	    final int high = is.readByte() & 0xff;
	    return (short)(high << 8 | low);
	  }

}
