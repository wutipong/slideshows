package com.playground_soft.slideshowsex.renderer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
public class StillImageRenderer
	implements GLSurfaceView.Renderer{

	
	private int texture;
	private int width = 0, height = 0;
	private int imageWidth = 0, imageHeight = 0;
	
	private final String vertexShaderCode = 
			"attribute vec4 a_position;\n"+ 
			"attribute vec2 a_texCoord;\n" +
			"varying vec2 v_texCoord;\n"+ 
			"void main()\n" + 
			"{ \n" + 
			"  gl_Position =  a_position; \n" + 
			"  v_texCoord = a_texCoord; \n" + 
			"}\n";

	private final String fragmentShaderCode = 
			"precision mediump float;\n"
			+ "varying vec2 v_texCoord;\n"
			+ "uniform sampler2D s_texture;\n"
			+ "void main()\n"
			+ "{\n" 
			+ "  if(v_texCoord.x < 0.0 || v_texCoord.x>1.0 ||" 
			+ "     v_texCoord.y <0.0 || v_texCoord.y > 1.0)" 
			+ "     gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);" 
			+ "  else " 
			+ "  {"
			+ "     gl_FragColor = texture2D(s_texture, v_texCoord); \n"
			+ "  }\n"
			+ "  \n"
			+ "}\n";
	private int program;
	private float[] uv = { 
			0f, 1f,
			0f, 0f,
			1f, 1f,
			1f, 0f 
	};
	private FloatBuffer uvBuffer = null;
	private static final float[] vertices = { 
		-1.0f, -1.0f, 0.0f, 
		-1.0f, 1.0f, 0.0f, 
		1.0f, -1.0f, 0.0f, 
		1.0f, 1.0f, 0.0f
	};
	private final FloatBuffer vertexBuffer;
	private int posHandle;
	private int textureHandle;
	private int uvHandle;
	
	private Bitmap bitmap;
	public StillImageRenderer(Bitmap bitmap)
	{
		this.bitmap = bitmap;
		imageWidth = bitmap.getWidth();
		imageHeight = bitmap.getHeight();
		
		vertexBuffer = GLUtil.createFloatBuffer(vertices);
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
		uvBuffer = GLUtil.createFloatBuffer(uv);
	}
	
	@Override
	public void onDrawFrame(GL10 arg0) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		
		GLES20.glUseProgram(program);
		posHandle = GLES20.glGetAttribLocation(program, "a_position");
		textureHandle = GLES20.glGetUniformLocation(program, "s_texture");
		uvHandle = GLES20.glGetAttribLocation(program, "a_texCoord");
		
		GLES20.glVertexAttribPointer(posHandle, 3, GLES20.GL_FLOAT, false, 0,
				vertexBuffer);
		GLES20.glEnableVertexAttribArray(posHandle);

		GLES20.glVertexAttribPointer(uvHandle, 2, GLES20.GL_FLOAT, false, 0,
				uvBuffer);
		GLES20.glEnableVertexAttribArray(uvHandle);
	
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
		GLES20.glUniform1i(textureHandle, 0);


		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE );
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE );

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
		
		GLES20.glFinish();
	}

	@Override
	public void onSurfaceChanged(GL10 arg0, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		this.width = width;
		this.height = height;
		
		updateUVBuffer();
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
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
		
		texture = GLUtil.loadTexture(bitmap);
		
		GLES20.glEnable (GLES20.GL_BLEND); 
		GLES20.glBlendFunc (GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public void cleanUp(){
		
	}
	

}
