package com.avatlantik.cooperative.adapter.treelistview;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.avatlantik.cooperative.adapter.ServicesListAdapter;
import com.avatlantik.cooperative.repository.DataRepository;
import com.avatlantik.cooperative.repository.DataRepositoryImpl;

import java.util.ArrayList;

public class TreeViewItemClickListener implements OnItemClickListener {
    private ServicesListAdapter treeViewAdapter;
    private DataRepository dataRepository;

    public TreeViewItemClickListener(Context context, ServicesListAdapter treeViewAdapter) {
        this.treeViewAdapter = treeViewAdapter;
        this.dataRepository = new DataRepositoryImpl(context.getContentResolver());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Element element = (Element) treeViewAdapter.getItem(position);
        ArrayList<Element> elements = treeViewAdapter.getElements();
        ArrayList<Element> elementsData = treeViewAdapter.getElementsData();
        if (!element.isHasChildren()) {
            return;
        }

        if (element.isExpanded()) {
            element.setExpanded(false);
            ArrayList<Element> elementsToDel = new ArrayList<Element>();
            for (int i = position + 1; i < elements.size(); i++) {
                if (element.getLevel() >= elements.get(i).getLevel())
                    break;
                elementsToDel.add(elements.get(i));
            }
            elements.removeAll(elementsToDel);
            treeViewAdapter.notifyDataSetChanged();
        } else {
            element.setExpanded(true);
            int i = 1;
            for (Element e : elementsData) {
                if (e.getParendId() == element.getId()) {
                    e.setExpanded(false);
                    elements.add(position + i, e);
                    i ++;
                }
            }
            treeViewAdapter.notifyDataSetChanged();
        }
    }


}