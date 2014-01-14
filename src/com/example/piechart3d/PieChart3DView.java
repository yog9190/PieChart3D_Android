/**
 * Copyright 2014 
 * 
 * Yogesh Pangam  
 *  
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this PieChart3DView software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.example.piechart3d;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * <b>GLSurfaceView<br/>
 * └PieChart3DView</b> <br/>
 * PieChart3DView is a custom 3D PiechartView , made to be independent of parent
 * application. To add this custom component in your design you just need to
 * drag and drop it in your layout.  <br/>
 * @author Yogesh Pangam
 * */
public class PieChart3DView extends GLSurfaceView implements OnGestureListener {
	static ArrayList<Sector> listSector = new ArrayList<PieChart3DView.Sector>();
	static boolean flag = true;
	TextView t1, t2, t3;
	float minX = 0, maxX = 65;
	float x1, x2, y1, y2, dx, dy, inix, iniz;
	private float d = 0f;
	public GestureDetectorCompat mDetector;
	private Pie3DRenderer mRenderer;
	private Context ctx;
	float centerx, centery;
	int totalAngle = 361;
	// OrientationSenor objOrientationSenor;
	boolean set = false;

	public PieChart3DView(Context context) {
		super(context);
		mRenderer = new Pie3DRenderer(context);
		mDetector = new GestureDetectorCompat(context, this);
		ctx = context;
		listSector.add(new Sector("111", 80, 0.5f, 0f, 0f));
		listSector.add(new Sector("222", 145, 0f, 0.0f, 0.5f));
		listSector.add(new Sector("333", 75, 0.0f, 0.5f, 0f));
		listSector.add(new Sector("444", 60, 0.5f, 0.8f, 0.9f));
	}

	public PieChart3DView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDetector = new GestureDetectorCompat(context, this);
		listSector.add(new Sector("111", 80, 0.5f, 0f, 0f));
		listSector.add(new Sector("222", 145, 0f, 0.0f, 0.5f));
		listSector.add(new Sector("333", 75, 0.0f, 0.5f, 0f));
		listSector.add(new Sector("444", 60, 0.5f, 0.8f, 0.9f));
		mRenderer = new Pie3DRenderer(context);
		ctx = context;
	}

	public void requestDoRender(float anglex, float anglez) {

		anglex = (anglex) % 360;
		anglez = (anglez) % 360;

		if (anglex < minX)
			anglex = minX;
		if (anglex > maxX)
			anglex = maxX;

		mRenderer.angle_x = anglex;
		mRenderer.angle_z = anglez;

		Log.d("appA", " " + mRenderer.angle_x + " " + mRenderer.angle_z);
		this.requestRender();
	}
	/**
	 * <b>public void initializePieChart(ArrayList<Sector> listSector) </b><br/>
	 * <br/>
	 * Initializes PieChart with List of Sectors<br/>
	 * @see #{@link Sector}
	 * */

	public void initializePieChart(ArrayList<Sector> listSector) {
		this.listSector = listSector;
		totalAngle = 0;
		if (listSector != null) {
			for (Sector s : listSector)
				totalAngle = totalAngle + s.degree;
		}
		totalAngle = totalAngle + 1;
		Log.d("app", "totalAngle" + totalAngle);
		mRenderer.calulatVertices();
		this.requestRender();
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
		ObjectAnimator mXAnimator = ObjectAnimator.ofFloat(mRenderer, "angle_x",
				0 , maxX);
		mXAnimator.setDuration(2000);

		mXAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				// progressBar.setProgress(progress);
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});		
		mXAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {

			}
		});
		LinearInterpolator di = new LinearInterpolator();
		mXAnimator.setInterpolator(di);
		mXAnimator.start();
	}
	/**
	 * <b>public void register()</b><br/>
	 * <br/>
	 * Registers PieChart3DView. It's needed to be called in
	 * activity's onResume() method.<br/>
	 * 
	 * @see {@link #unregister()}
	 * 
	 * */
	public void register() {
		if (!set) {
			setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			setRenderer(mRenderer);
			getHolder().setFormat(PixelFormat.TRANSLUCENT);
			setZOrderOnTop(true);
			set = true;
		}
	}

	/**
	 * <b>public void unregister()</b><br/>
	 * <br/>
	 * Unregisters PieChart3DView. It's needed to be called in activity's onPause () method.
	 * 
	 * @see {@link #register()}
	 * */
	public void unregister() {

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(widthSize, heightSize);
		centerx = widthSize * 0.5f;
		centery = heightSize * 0.5f;
	}

	// -------------------------------------------------------------------------------------
	public class Pie3DRenderer implements GLSurfaceView.Renderer {
		Context mContext;
		Slice objSlice;
		float angle_x = 0;
		public float getAngle_x() {
			return angle_x;
		}

		public void setAngle_x(float angle_x) {
			this.angle_x = angle_x;
			PieChart3DView.this.requestDoRender(angle_x, angle_z);
		}

		float angle_z = 0;

		public float getAngle_z() {
			return angle_z;
		}

		public void setAngle_z(float angle_z) {
			this.angle_z = angle_z;
			PieChart3DView.this.requestDoRender(angle_x, angle_z);
		}

		Map<Integer, Slice> myMap = new ConcurrentHashMap<Integer, PieChart3DView.Slice>();
		private int points = 361;
		private float vertices[] = { 0.0f, 0.0f, 0.0f };
		float radious = 0.9f;

		// Lighting (NEW)
		boolean lightingEnabled = true; // Is lighting on? (NEW)

		Pie3DRenderer(Context context) {
			super();
			mContext = context;

			calulatVertices();
		}

		public void calulatVertices() {
			points = 361;
			vertices = new float[(points + 1) * 3];

			for (int i = 0; i < (points); i++) {
				vertices[(i * 3) + 0] = 1f * ((float) (radious * Math.cos(Math
						.toRadians((float) i))));
				vertices[(i * 3) + 1] = 1f * (float) (radious * Math.sin(Math
						.toRadians((float) i)));
				vertices[(i * 3) + 2] = 0f;
			}

			int temp = 0;
			Sector objSector = PieChart3DView.listSector.get(0);
			int cnt = 0;
			for (int j = 0; j < points; j++) {

				float color[] = new float[4];
				float[] vert = new float[4 * 3];
				vert[0] = 0;
				vert[1] = 0;
				vert[2] = 0;
				vert[3] = vertices[(j * 3) + 0];
				vert[4] = vertices[(j * 3) + 1];
				vert[5] = vertices[(j * 3) + 2];
				vert[6] = vertices[((j + 1) * 3) + 0];
				vert[7] = vertices[((j + 1) * 3) + 1];
				vert[8] = vertices[((j + 1) * 3) + 2];

				if (j > temp + objSector.degree - 1) {
					cnt++;
					if (cnt > PieChart3DView.listSector.size() - 1)
						cnt = PieChart3DView.listSector.size() - 1;
					objSector = PieChart3DView.listSector.get(cnt);
					temp = j;
				}

				color[0] = objSector.r;
				color[1] = objSector.g;
				color[2] = objSector.b;
				color[3] = 0.9f;

				if (j == points - 1) {
					vert[6] = 0.9f;
					vert[7] = 0;
					vert[8] = 0;
				}

				objSlice = new Slice(vert, color, true);
				myMap.put(j, objSlice);
			}

		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {

			// gl.glEnable(GL10.GL_CULL_FACE); // enable the differentiation of
			// which side may be visible
			gl.glShadeModel(GL10.GL_SMOOTH);

			// gl.glFrontFace(GL10.GL_CCW); // which is the front? the one which
			// is
			// drawn counter clockwise
			// gl.glCullFace(GL10.GL_BACK); // which one should NOT be drawn

			gl.glEnable(GL10.GL_LINE_SMOOTH);

			gl.glEnable(GL10.GL_DEPTH_TEST);

			gl.glDepthFunc(GL10.GL_LEQUAL);

			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

			float[] lightAmbient = { 0.4f, 0.4f, 0.4f, 1.0f };
			float[] lightDiffuse = { 0.8f, 0.8f, 0.8f, 1.0f };
			float[] specularLight = { 0.5f, 0.5f, 0.5f, 1.0f };
			float[] lightPosition = { +.9f, +0.5f, +.9f, 1.0f };

			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularLight, 0);
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0);
			gl.glEnable(GL10.GL_LIGHT0);
		}

		public void onDrawFrame(GL10 gl) {
			// Log.d("app", "On Draw Frame");
			gl.glMatrixMode(GL10.GL_MODELVIEW);

			gl.glLoadIdentity();
			gl.glPushMatrix();

			gl.glClearColor(0, 0, 0, 0f);

			gl.glClear(GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_COLOR_BUFFER_BIT
					| GL10.GL_STENCIL_BUFFER_BIT);

			if (lightingEnabled) {
				gl.glEnable(GL10.GL_LIGHTING);
			} else {
				gl.glDisable(GL10.GL_LIGHTING);
			}

			// float vertices[] = { 0, 0, 0, 0f, -(float)
			// Math.sin(Math.toRadians(angle_x + 180)), (float)
			// Math.cos(Math.toRadians(angle_x + 180)) };
			//
			// float vertices[] = { 0, 0, 0, +.9f, +0.5f, +.9f };
			// short indices[] = { 0, 1 };
			// ShortBuffer indicesBuffer1;
			// FloatBuffer verticesBuffer = null;
			// ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
			// ibb.order(ByteOrder.nativeOrder());
			// indicesBuffer1 = ibb.asShortBuffer();
			// indicesBuffer1.put(indices);
			// indicesBuffer1.position(0);
			// ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			// vbb.order(ByteOrder.nativeOrder());
			// verticesBuffer = vbb.asFloatBuffer();
			// verticesBuffer.put(vertices);
			// verticesBuffer.position(0);
			//
			// float matAmbient[] = new float[] { 1f, 1f, 0f, 0.9f };
			// float matDiffuse[] = new float[] { 1f, 1f, 0f, 0.9f };
			// float matSpec[] = new float[] { 1f, 1f, 0f, 0.9f };
			//
			// gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
			// matDiffuse, 0);
			// gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
			// matAmbient, 0);
			// gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
			// matSpec,
			// 0);

			// gl.glColor4f(1f, 1f, 0f, 0.9f);
			// gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
			// gl.glDrawElements(GL10.GL_LINES, 2, GL10.GL_UNSIGNED_SHORT,
			// indicesBuffer1);
			// gl.glPopMatrix();

			// gl.glLoadIdentity();
			// gl.glPushMatrix();

			gl.glRotatef(angle_z, 0f,
					-(float) Math.sin(Math.toRadians(angle_x + 180)),
					(float) Math.cos(Math.toRadians(angle_x + 180)));

			gl.glRotatef(angle_x, 1, 0, 0);

			Iterator<Integer> it1 = myMap.keySet().iterator();
			while (it1.hasNext()) {
				Integer key = it1.next();
				myMap.get(key).draw(gl);
			}
			gl.glPopMatrix();
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			gl.glViewport(0, 0, w, h);
		}

		public void clearBuffers(boolean color, boolean depth, boolean stencil) {
			int bits = 0;
			if (color) {
				bits = GLES20.GL_COLOR_BUFFER_BIT;
			}
			if (depth) {
				bits |= GLES20.GL_DEPTH_BUFFER_BIT;
			}
			if (stencil) {
				bits |= GLES20.GL_STENCIL_BUFFER_BIT;
			}
			if (bits != 0) {
				GLES20.glClear(bits);
			}
		}

	}

	// ------------------------------------------------------
	/**
	 * <b>Sector</b><br/>
	 * <br/>
	 * Represents a Sector from a pie chart.
	 * 
	 * @see {@link #initializePieChart(ArrayList)}
	 * */
	public static class Sector {
		String name;
		int degree;
		float r;
		float g;
		float b;
		/**
		 * <b>public Sector(String name, int degree, float r, float g, float b)</b><br/>
		 * <br/>
		 *@param name - name of the sector.
		 *@param degree - angle occupied by sector in pie chart.
		 *@param r,g,b - color of the sector (RGB) normalized in (0-1) code
		 *
		 * */
		public Sector(String name, int degree, float r, float g, float b) {
			this.name = name;
			this.degree = degree;
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}

	// ---------------------------------------------------------------

	public static class Slice {
		private float vertices[] = new float[(6) * 3];
		float color[];
		boolean necessary = true;

		short indices[] = { 0, 1, 2, 3, 5, 4 };
		short indices1[] = { 1, 4, 2, 2, 4, 5 };
		ShortBuffer indicesBuffer1;
		private FloatBuffer verticesBuffer = null;

		// Our index buffer.
		private ShortBuffer indicesBuffer = null;

		public Slice(float vetices[], float Color[], boolean necessary) {
			this.necessary = necessary;
			color = Color;
			for (int i = 0; i < (3); i++) {
				vertices[(i * 3) + 0] = vetices[(i * 3) + 0];
				vertices[(i * 3) + 1] = vetices[(i * 3) + 1];
				vertices[(i * 3) + 2] = -0.2f;

				vertices[(i * 3) + 9] = vetices[(i * 3) + 0];
				vertices[(i * 3) + 10] = vetices[(i * 3) + 1];
				vertices[(i * 3) + 11] = 0.2f;
			}

			if (!PieChart3DView.flag) {
				for (int i = 0; i < (6); i++) {
					Log.d("ver" + i, "" + vertices[(i * 3) + 0]);
					Log.d("ver" + i, "" + vertices[(i * 3) + 1]);
					Log.d("ver" + i, "" + vertices[(i * 3) + 2]);
				}
				PieChart3DView.flag = true;
			}

			setVertices(vertices);
			setIndices(indices);
			ByteBuffer ibb = ByteBuffer.allocateDirect(indices1.length * 2);
			ibb.order(ByteOrder.nativeOrder());
			indicesBuffer1 = ibb.asShortBuffer();
			indicesBuffer1.put(indices1);
			indicesBuffer1.position(0);
		}

		public void draw(GL10 gl) {
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);

			float matAmbient[] = new float[] { color[0], color[1], color[2],
					1.0f };
			float matDiffuse[] = new float[] { color[0], color[1], color[2],
					1.0f };
			float matSpec[] = new float[] { color[0], color[1], color[2], 1.0f };

			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
					matDiffuse, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
					matAmbient, 0);
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, matSpec,
					0);
			/*
			 * else{ gl.glDepthMask(false); //gl.glDisable(GL10.GL_LIGHTING);
			 * gl.glEnable(gl.GL_BLEND);
			 * gl.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA); }
			 */

			// gl.glColor4f(color[0], color[1], color[2], color[3]);
			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT,
					indicesBuffer);
			// gl.glColor4f(color[0], color[1], color[2], color[3] - 0.2f);
			gl.glDrawElements(GL10.GL_TRIANGLES, 6, GL10.GL_UNSIGNED_SHORT,
					indicesBuffer1);
		}

		protected void setVertices(float[] vertices) {
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			verticesBuffer = vbb.asFloatBuffer();
			verticesBuffer.put(vertices);
			verticesBuffer.position(0);
		}

		protected void setIndices(short[] indices) {
			ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
			ibb.order(ByteOrder.nativeOrder());
			indicesBuffer = ibb.asShortBuffer();
			indicesBuffer.put(indices);
			indicesBuffer.position(0);
		}
	}

	float mAngle;
	float iniMAngle;

	// ----------------------------------------------
	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true);
		float xxx = event.getX() - centerx;
		float yyy = event.getY() - centery;

		{
			mDetector.onTouchEvent(event);
			String direction;
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case (MotionEvent.ACTION_DOWN):
				if (mZAnimator != null)
					mZAnimator.cancel();
				iniMAngle = (float) java.lang.Math.atan2(yyy, xxx);
				inix = mRenderer.angle_x;
				iniz = mRenderer.angle_z;
				x1 = event.getX();
				y1 = event.getY();
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				iniMAngle = (float) java.lang.Math.atan2(yyy, xxx);
				Log.d("appAS", "Pointer down");
				inix = mRenderer.angle_x;
				iniz = mRenderer.angle_z;
				d = rotation(event);
				break;
			case MotionEvent.ACTION_POINTER_UP:
				inix = mRenderer.angle_x;
				iniz = mRenderer.angle_z;
				iniMAngle = (float) java.lang.Math.atan2(yyy, xxx);
				break;
			case (MotionEvent.ACTION_MOVE): {
				if (event.getPointerCount() == 1) {
					x2 = event.getX();
					y2 = event.getY();
					dx = x2 - x1;
					dy = y2 - y1;
					if (Math.abs(dx) < Math.abs(dy) && Math.abs(dy) > 25) {
						iniz = mRenderer.angle_z;
						if (dy > 0)
							direction = "down";
						else
							direction = "up";
						this.requestDoRender(inix - (dy / 3), iniz);
					} else {
						mAngle = (float) java.lang.Math.atan2(yyy, xxx);
						inix = mRenderer.angle_x;
						if (dx > 0)
							direction = "right";
						else
							direction = "left";
						this.requestDoRender(
								inix, /* iniz + (dx / 3) */
								iniz+(float) Math.toDegrees((mAngle - iniMAngle)));
						Log.d("ang", "" + iniz + " "
								+ (iniz+(float) Math.toDegrees((mAngle - iniMAngle))));
					}
				} else if (event.getPointerCount() == 2) {
					Log.d("app", "Rotating " + event.getPointerCount());
					float rot = rotation(event) - d;
					this.requestDoRender(inix, iniz + rot);
				}
			}
			}
		}
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub

		if (Math.abs(e1.getX() - e2.getX()) > /* SWIPE_MIN_DISTANCE */200
				&& Math.abs(velocityX) > /* SWIPE_THRESHOLD_VELOCITY */200) {
			Log.d("appOnF", "onFling");
			int i = 1;
			if (mAngle > 0)
				i = -1;
			animateV((int) (i * velocityX / 1000));
		}

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

	ObjectAnimator mZAnimator;

	@SuppressLint("NewApi")
	public void animateV(float velocity) {
		Log.d("appOnF", "Cancle");
		if (mZAnimator != null)
			mZAnimator.cancel();
		mZAnimator = ObjectAnimator.ofFloat(mRenderer, "angle_z",
				mRenderer.angle_z + (60 * velocity));
		mZAnimator.setDuration(2000);

		mZAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				// progressBar.setProgress(progress);
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});

		// mProgressBarAnimator.reverse();
		mZAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {

			}
		});
		DecelerateInterpolator di = new DecelerateInterpolator(1.0f);
		mZAnimator.setInterpolator(di);
		mZAnimator.start();
	}

	public void cancelAnimation() {
		if (mZAnimator != null)
			mZAnimator.cancel();
	}
}
