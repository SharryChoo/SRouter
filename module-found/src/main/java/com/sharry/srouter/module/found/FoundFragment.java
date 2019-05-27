package com.sharry.srouter.module.found;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sharry.srouter.annotation.Query;
import com.sharry.srouter.annotation.Route;
import com.sharry.srouter.module.base.ModuleConstants;
import com.sharry.srouter.support.facade.SRouter;


/**
 * A simple {@link Fragment} subclass.
 */
@Route(
        authority = ModuleConstants.Found.NAME,
        path = ModuleConstants.Found.FOUND_FRAGMENT,
        desc = "组件1的入口页面"
)
public class FoundFragment extends Fragment {

    @Query(key = "title")
    String title;

    @Query(key = "content")
    String content;


    public FoundFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SRouter.bindQuery(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.found_fragment_found, container, false);
        TextView textView = view.findViewById(R.id.tv_center_text);
        textView.setText("title = " + title + ", content = " + content);
        return view;
    }

}
