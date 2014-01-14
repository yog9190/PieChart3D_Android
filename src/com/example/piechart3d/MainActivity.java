package com.example.piechart3d;

import java.util.ArrayList;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;

import com.example.piechart3d.PieChart3DView.Sector;

public class MainActivity extends Activity {
	PieChart3DView objPieChart3DView;
	ArrayList<Sector> listSector ;
	
	Button buttonUpdate;
	boolean flag=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		objPieChart3DView = (PieChart3DView) findViewById(R.id.pieChartDView1);
		
		buttonUpdate=(Button)findViewById(R.id.buttonUpdate);
		buttonUpdate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(flag){
					listSector = new ArrayList<PieChart3DView.Sector>();
					listSector.add(new Sector("sector1", 80, 0.5f, 0f, 0f));
					listSector.add(new Sector("sector2", 145, 0f, 0.0f, 0.5f));
					listSector.add(new Sector("sector3", 60, 0.0f, 0.5f, 0f));
					listSector.add(new Sector("sector4", 60, 0.5f, 0.8f, 0.9f));
					objPieChart3DView.initializePieChart(listSector);
					flag=false;
					Log.d("app","true");
				}else{
					listSector = new ArrayList<PieChart3DView.Sector>();
					listSector.add(new Sector("sector1", 60, 0.5f, 0f, 0f));
					listSector.add(new Sector("sector2", 40, 0.5f, 0.2f, 0.9f));
					listSector.add(new Sector("sector3", 20, 0.2f, 0.8f, 0.4f));
					listSector.add(new Sector("sector4", 80, 0f, 0.0f, 0.5f));
					listSector.add(new Sector("sector5", 40, 0.0f, 0.5f, 0f));
					listSector.add(new Sector("sector6", 50, 0.9f, 0.5f, 0f));
					listSector.add(new Sector("sector7", 20, 0.0f, 0.5f, 0.9f));
					listSector.add(new Sector("sector8", 50, 0.5f, 0.8f, 0.9f));
					objPieChart3DView.initializePieChart(listSector);
					flag=true;
					Log.d("app","false");
				}
			}
		});		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();	
		objPieChart3DView.register();
		objPieChart3DView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	@Override
	protected void onPause() {
		super.onPause();		
		objPieChart3DView.unregister();
	}	

}
