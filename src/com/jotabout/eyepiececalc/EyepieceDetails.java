package com.jotabout.eyepiececalc;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class EyepieceDetails extends Activity {
	
	TextView mMag;
	TextView mActualFOV;
	TextView mApparentFOV;
	TextView mExitPupil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.ep_details);
        
        mMag         = (TextView) findViewById( R.id.ep_mag );
        mActualFOV   = (TextView) findViewById( R.id.ep_actual_fov );
        mApparentFOV = (TextView) findViewById( R.id.ep_apparent_fov );
        mExitPupil   = (TextView) findViewById( R.id.ep_exit_pupil );
        
        Bundle extras = getIntent().getExtras();
        calcAndDisplay( extras.getString( EyepieceCalc.PARAM_SCOPE_FL ),
        		extras.getString( EyepieceCalc.PARAM_SCOPE_AP ),
        		extras.getString( EyepieceCalc.PARAM_EP_FL ),
        		extras.getString( EyepieceCalc.PARAM_EP_AFOV )
        		);
	}
	
	private void calcAndDisplay( String scope_fl, String scope_ap, String ep_fl, String ep_afov )
	{
		// TODO trap exceptions for parse errors
		// TODO decouple calculations from display
		float scopeFl = Float.parseFloat( scope_fl );
		float scopeAp = Float.parseFloat( scope_ap );
		float epFl = Float.parseFloat( ep_fl );
		float epAFOV = Float.parseFloat( ep_afov );
		
		float magnification = scopeFl / epFl;
		float exitPupil = scopeAp / magnification;
		float actualFOV = epAFOV / magnification; 
		
		mMag.setText( String.valueOf(magnification) );
		mApparentFOV.setText( ep_afov );
		mActualFOV.setText( String.valueOf( actualFOV ));
		mExitPupil.setText( String.valueOf( exitPupil ));
	}

}
