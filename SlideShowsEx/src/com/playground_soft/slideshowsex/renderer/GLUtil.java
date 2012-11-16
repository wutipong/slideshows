package com.playground_soft.slideshowsex.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class GLUtil {
	public static int loadTexture(Bitmap bitmap) {

		int textures[] = new int[1];

		
		Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true);

		GLES20.glGenTextures(textures.length, textures, 0);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
		// Set filtering
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap2, 0);

		bitmap2.recycle();

		return textures[0];

	}
	
	public static void unloadTexture(int texture)  {
		int[] textures = new int[1];
		textures[0] = texture;
		GLES20.glDeleteTextures(1, textures, 0);
	}
	
	public static FloatBuffer createFloatBuffer(float[] input) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(input.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuffer.asFloatBuffer();
		buffer.put(input);

		buffer.position(0);
		return buffer;
	}
}
