package com.jotabout.eyepiececalc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EyepieceCalc extends Activity {
	
	EditText mScopeFocalLength;
	EditText mScopeAperture;
	EditText mEyepieceFocalLength;
	EditText mEyepieceApparentFOV;
	
	public static final String PARAM_SCOPE_FL = "com.jotabout.eyepiececalc.scope_fl";
	public static final String PARAM_SCOPE_AP = "com.jotabout.eyepiececalc.scope_ap";
	public static final String PARAM_EP_FL    = "com.jotabout.eyepiececalc.ep_fl";
	public static final String PARAM_EP_AFOV  = "com.jotabout.eyepiececalc.ep_afov";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mScopeFocalLength    = (EditText) findViewById( R.id.scope_fl );
        mScopeAperture       = (EditText) findViewById( R.id.scope_ap );
        mEyepieceFocalLength = (EditText) findViewById( R.id.ep_fl );
        mEyepieceApparentFOV = (EditText) findViewById( R.id.ep_afov );
        
        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.calculate);
        button.setOnClickListener(mCalcListener);
    }
    
    private OnClickListener mCalcListener = new OnClickListener() {
        public void onClick(View v) {
        	// get items from fields, and pass it to the EyepieceDetails class
        	Intent intent = new Intent(EyepieceCalc.this, EyepieceDetails.class);
        	intent.putExtra(PARAM_SCOPE_FL, mScopeFocalLength.getText().toString());
        	intent.putExtra(PARAM_SCOPE_AP, mScopeAperture.getText().toString());
        	intent.putExtra(PARAM_EP_FL, mEyepieceFocalLength.getText().toString());
        	intent.putExtra(PARAM_EP_AFOV, mEyepieceApparentFOV.getText().toString());
        	
            // Request the next activity transition (here starting a new one).
            startActivity(intent);
        }
    };
}