package com.playground_soft.slideshowsex;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Sprite {
	private int texture;
	private int alphamap;
	private int program;
	private float[] transformations = {
		1.0f, 0.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 0.0f, 0.0f,
		0.0f, 0.0f, 1.0f, 0.0f,
		0.0f, 0.0f, 0.0f, 1.0f
	};
	private FloatBuffer uvBuffer = null;
	
	private static final FloatBuffer vertexBuffer;
	private int width = 0, height = 0;
	private int imageWidth = 0, imageHeight = 0;
	private long start = 0;
	private static final float[] vertices = { 
		-1.0f, -1.0f, 0.0f, 
		-1.0f, 1.0f, 0.0f, 
		1.0f, -1.0f, 0.0f, 
		1.0f, 1.0f, 0.0f
	};
	
	static {
		vertexBuffer = createFloatBuffer(vertices);
	}
	private final String vertexShaderCode = 
			"attribute vec4 a_position;\n"+ 
			"attribute vec2 a_texCoord;\n" +
			"uniform mat4 u_transform;\n" +
			"varying vec2 v_texCoord;\n"+ 
			"void main()\n" + 
			"{ \n" + 
			"  gl_Position =u_transform * a_position; \n" + 
			"  v_texCoord = a_texCoord; \n" + 
			"}\n";

	private final String fragmentShaderCode = 
			"precision mediump float;\n"
			+ "varying vec2 v_texCoord;\n"
			+ "uniform sampler2D s_texture;\n"
			+ "uniform sampler2D s_alphamap;\n"
			+ "uniform float alphafactor; \n"
			+ "void main()\n"
			+ "{\n" 
			+ "  if(v_texCoord.x < 0.0 || v_texCoord.x>1.0 ||" 
			+ "     v_texCoord.y <0.0 || v_texCoord.y > 1.0)" 
			+ "     gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);" 
			+ "  else " 
			+ "  {"
			+ "     gl_FragColor = texture2D(s_texture, v_texCoord); \n"
			+ "  }\n"
			+ "  gl_FragColor.a = clamp((gl_FragColor.a * texture2D(s_alphamap, v_texCoord).r) + alphafactor, 0.0, 1.0); \n"
			+ "}\n";
	
	private float[] uv = { 
			0f, 1f,
			0f, 0f,
			1f, 1f,
			1f, 0f 
	};

	public static Sprite fromResource(Resources resources, int id){
		Bitmap bitmap = BitmapFactory
				.decodeResource(resources, id); 
		
		return new Sprite(bitmap);
	}
	
	
	public Sprite(Bitmap bitmap) {
		texture = loadTexture(bitmap);
		imageWidth = bitmap.getWidth();
		imageHeight = bitmap.getHeight();
		
		int vertexshader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		GLES20.glShaderSource(vertexshader, vertexShaderCode);
		GLES20.glCompileShader(vertexshader);

		int fragmentshader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(fragmentshader, fragmentShaderCode);
		GLES20.glCompileShader(fragmentshader);

		program = GLES20.glCreateProgram();
		GLES20.glAttachShader(program, vertexshader);
		GLES20.glAttachShader(program, fragmentshader);

		GLES20.glLinkProgram(program);
		
		Bitmap b = Bitmap.createBitmap(1, 1, Config.ARGB_8888);
		b.setPixel(0, 0, 0xffffffff);
		this.alphamap = loadTexture(b);
		b.recycle();
		updateUVBuffer();

	}
	
	public void setTransformation(float[] transformations){
		this.transformations = Arrays.copyOf(transformations, 16);
	}
	
	public float[] getTransformation(){
		return Arrays.copyOf(transformations, 16);
	}
	
	public void draw() {
		GLES20.glUseProgram(program);
		int posHandle = GLES20.glGetAttribLocation(program, "a_position");
		int textureHandle = GLES20.glGetUniformLocation(program, "s_texture");
		int uvHandle = GLES20.glGetAttribLocation(program, "a_texCoord");
		int transformHandle = GLES20.glGetUniformLocation(program, "u_transform");
		int alphaHandle = GLES20.glGetUniformLocation(program, "s_alphamap");
		int alphaFactorHandle = GLES20.glGetUniformLocation(program, "alphafactor");
		
		GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0,
				vertexBuffer);
		GLES20.glEnableVertexAttribArray(posHandle);

		GLES20.glVertexAttribPointer(uvHandle, 2, GLES20.GL_FLOAT, false, 0,
				uvBuffer);
		GLES20.glEnableVertexAttribArray(uvHandle);
		
		GLES20.glUniformMatrix4fv(transformHandle, 1, false, transformations, 0);

		if(start == 0) start = System.currentTimeMillis();
		float factor = (float)(System.currentTimeMillis() - start)/2000.0f;
		
		GLES20.glUniform1f(alphaFactorHandle, factor);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glUniform1i(textureHandle, 0);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, alphamap);
		GLES20.glUniform1i(alphaHandle, 1);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE );
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE );

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		
	}
	
	public void setAlphaMap(Bitmap bitmap) {
		alphamap = loadTexture(bitmap);
	}
	private static int loadTexture(Bitmap bitmap) {

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
	
	private static FloatBuffer createFloatBuffer(float[] input) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(input.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer buffer = byteBuffer.asFloatBuffer();
		buffer.put(input);

		buffer.position(0);
		return buffer;
	}

	private void updateUVBuffer() {
		
		if( width != 0 && height !=0 ){
			float ratio = ((float)imageWidth) / ((float) imageHeight);
			float screenRatio = ((float)width) / ((float)height);
			float left, right, top, bottom;
			if(ratio < screenRatio) {
				float targetHeight = (width * imageHeight) / imageWidth;
				float deltaHeight = Math.abs(height - targetHeight);
				float excess = (deltaHeight/2)/height;
				right = 1.0f + excess;
				left = 0.0f - excess;
				bottom = 0.0f;
				top = 1.0f;
			} else {
				float targetWidth = (height * imageWidth) / imageHeight;
				float deltaWidth = Math.abs(width - targetWidth);
				float excess = (deltaWidth/2)/width;
				top = 1.0f + excess;
				bottom = 0.0f - excess;
				left = 0.0f;
				right = 1.0f;
			}
			
			float[] newuv = {
					left, top,
					left, bottom,
					right, top,
					right, bottom 
			};
			
			uv = newuv;
		}
		uvBuffer = createFloatBuffer(uv);
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		
		updateUVBuffer();
	}
}
