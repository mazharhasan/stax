package com.smarttaxi.driver.fragments;

import java.util.ArrayList;
import java.util.List;

import android.R.style;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import com.smarttaxi.driver.R;
import com.smarttaxi.driver.R.string;
import com.smarttaxi.driver.adapters.PickupRequestAdapter;
import com.smarttaxi.driver.entities.Pick;
import com.viewpagerindicator.CirclePageIndicator;

public class CustomDialogFragment extends DialogFragment {
	
	Pick _pick;
	public static CirclePageIndicator mIndicator;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    setStyle(DialogFragment.STYLE_NORMAL, style.Theme_Black_NoTitleBar_Fullscreen);
	    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
    	View   view =    inflater.inflate(R.layout.fragment_dialogue, container);
    	ViewPager vp_contentAcoesMusculares_SequenciaExercicios = (ViewPager) view.findViewById(R.id.vp_contentAcoesMusculares_SequenciaExercicios);
    	mIndicator = (CirclePageIndicator)view.findViewById(R.id.indicator);
         
		List<Fragment> fragments = getFragments();
       
		PickupRequestAdapter ama = new PickupRequestAdapter(getChildFragmentManager(), fragments);
        vp_contentAcoesMusculares_SequenciaExercicios.setAdapter(ama);
        mIndicator.setViewPager(vp_contentAcoesMusculares_SequenciaExercicios);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return view;
    }
    
    
    
    private List<Fragment> getFragments(){
        List<Fragment> fList = new ArrayList<Fragment>();
        
        _pick = new Pick();
        _pick.AdditionalMessage ="addd";
        _pick.journey_id ="445";
        
            fList.add(PickupRequestFragment.newInstance("Fragment 1",1,_pick));
            fList.add(PickupRequestFragment.newInstance("Fragment 2",2,_pick));
            fList.add(PickupRequestFragment.newInstance("Fragment 3",3,_pick));
        return fList;
    }
	
    /*@Override
    public void onDismiss(DialogInterface dialog) {
      if (getActivity() != null && getActivity() instanceof Dismissed) {
        ((Dismissed) getActivity()).dialogDismissed();
      }
      super.onDismiss(dialog);
    }*/

   /* public interface Dismissed {
      public void dialogDismissed();
    }*/
	

   
}
