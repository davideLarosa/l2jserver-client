package com.l2client.util;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * Copyright (c) 2008-2010 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

//TODO check used at all? if not remove it
public class TangentUtil {
	
    private static interface IndexWrapper {
        public int get(int i);
        public short getShort(int i);
        public int size();
    }
    
    private static IndexWrapper getIndexWrapper(final Buffer buff) {
        if (buff instanceof ShortBuffer) {
            return new IndexWrapper() {
                private ShortBuffer buf = (ShortBuffer) buff;
                public int get(int i) {
                    return ((int) buf.get(i))&(0x0000FFFF);
                }
                public short getShort(int i){return buf.get(i);}
                public int size() {
                    return buf.capacity();
                }
            };
        }
        else if (buff instanceof IntBuffer) {
            return new IndexWrapper() {
                private IntBuffer buf = (IntBuffer) buff;
                public int get(int i) {
                    return buf.get(i);
                }
                public int size() {
                    return buf.capacity();
                }
				@Override
				public short getShort(int i) {
					return 0;
				}
            };
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public static FloatBuffer generateTangentBuffer(final Mesh meshData) {
        final FloatBuffer vertexBuffer = (FloatBuffer) meshData.getBuffer(Type.Position).getData();
        final FloatBuffer normalBuffer = (FloatBuffer) meshData.getBuffer(Type.Normal).getData();
        final FloatBuffer textureBuffer = (FloatBuffer) meshData.getBuffer(Type.TexCoord).getData();
        IndexWrapper indexBuffer = getIndexWrapper(meshData.getBuffer(Type.Index).getData());

        final int vertexCount = meshData.getVertexCount();
        final int triangleCount = meshData.getTriangleCount();

        final Vector3f[] tan1 = new Vector3f[vertexCount];
        final Vector3f[] tan2 = new Vector3f[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            tan1[i] = new Vector3f();
            tan2[i] = new Vector3f();
        }

        final Vector3f[] vertex = BufferUtils.getVector3Array(vertexBuffer);
        final Vector3f[] normal = BufferUtils.getVector3Array(normalBuffer);
        final Vector2f[] texcoord = BufferUtils.getVector2Array(textureBuffer);

        for (int a = 0; a < triangleCount; a++) {
            final int i1 = indexBuffer.get(a * 3);
            final int i2 = indexBuffer.get(a * 3 + 1);
            final int i3 = indexBuffer.get(a * 3 + 2);

            final Vector3f v1 = vertex[i1];
            final Vector3f v2 = vertex[i2];
            final Vector3f v3 = vertex[i3];

            final Vector2f w1 = texcoord[i1];
            final Vector2f w2 = texcoord[i2];
            final Vector2f w3 = texcoord[i3];

            final float x1 = v2.x - v1.x;
            final float x2 = v3.x - v1.x;
            final float y1 = v2.y - v1.y;
            final float y2 = v3.y - v1.y;
            final float z1 = v2.z - v1.z;
            final float z2 = v3.z - v1.z;

            final float s1 = w2.x - w1.x;
            final float s2 = w3.x - w1.x;
            final float t1 = w2.y - w1.y;
            final float t2 = w3.y - w1.y;

            final float r = 1.0F / (s1 * t2 - s2 * t1);
            final Vector3f sdir = new Vector3f((t2 * x1 - t1 * x2) * r, (t2 * y1 - t1 * y2) * r, (t2 * z1 - t1 * z2) * r);
            final Vector3f tdir = new Vector3f((s1 * x2 - s2 * x1) * r, (s1 * y2 - s2 * y1) * r, (s1 * z2 - s2 * z1) * r);

            tan1[i1].addLocal(sdir);
            tan1[i2].addLocal(sdir);
            tan1[i3].addLocal(sdir);

            tan2[i1].addLocal(tdir);
            tan2[i2].addLocal(tdir);
            tan2[i3].addLocal(tdir);
        }

        final FloatBuffer tangentBuffer = BufferUtils.createFloatBuffer(vertexCount*4);

        final Vector3f calc1 = new Vector3f();
        final Vector3f calc2 = new Vector3f();
        for (int a = 0; a < vertexCount; a++) {
            final Vector3f n = normal[a];
            final Vector3f t = tan1[a];

            // Gram-Schmidt orthogonalize
            float dot = n.dot(t);
            calc1.set(t).subtractLocal(n.mult(dot, calc2)).normalizeLocal();
            tangentBuffer.put(calc1.x).put(calc1.y).put(calc1.z);

            // Calculate handedness
            dot = calc1.set(n).crossLocal(t).dot(tan2[a]);
            final float w = dot < 0.0f ? -1.0f : 1.0f;
            tangentBuffer.put(w);
        }

        return tangentBuffer;
    }

	public static void addTangentsToMesh(Mesh mesh) {
		FloatBuffer buf = generateTangentBuffer(mesh);
		mesh.setBuffer(Type.Tangent, 4, buf);
	}
}
