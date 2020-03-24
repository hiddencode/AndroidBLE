package com.example.androidble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/*
* This class realize adapter for expandable list
*/
public class ExpandableListAdapter extends BaseExpandableListAdapter {


    private ArrayList<ArrayList<String>> mGroups;
    private Context mContext;
    private String NameGroup;

    /* constructor for adapter
     * @param context      -- Context of Activity
     * @param groups       -- List items of group
     * @param name         -- Name of group
     */
    ExpandableListAdapter(Context context, ArrayList<ArrayList<String>> groups, String name) {
        mContext = context;
        mGroups = groups;
        NameGroup = name;
    }

    /*
     * @note: unused, for stub overriding
     * return size of group
     */
    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    /*
     * @note: unused, for stub overriding
     * return count of item in group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).size();
    }

    /*
     * @note: unused, for stub overriding
     * return group position
     */
    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    /*
     * @note: unused, for stub overriding
     * return item position
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).get(childPosition);
    }

    /*
     * @note: unused, for stub overriding
     * return group id
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /*
     * @note: unused, for stub overriding
     * return item id
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /*
     * @note: unused, for stub overriding
     * return stable or not
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
     * @note: unused
     * Getting view of group
     * @param groupPosition    --  position group
     * @param isExpanded       -- check expandable list or not
     * @convertView            -- view which to convert
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            convertView = inflater.inflate(R.layout.group_view, null);
        }

        TextView textGroup = convertView.findViewById(R.id.group_text);
        textGroup.setText(NameGroup);

        return convertView;

    }

    /*
     * @note: unused
     * Getting item view
     * @param groupPosition     -- position group
     * @param childPosition     -- position item
     * @param isLastChild       -- check last item or not
     * @param convertView       -- view which to convert
     * @param parent            -- view of group (parent)
     *
     * @return convertView      -- converted view
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_view, null);
        }
        TextView textChild = convertView.findViewById(R.id.item_text);
        textChild.setText(mGroups.get(groupPosition).get(childPosition));

        return convertView;
    }

    /*
     * @note: unused, stub for override
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}