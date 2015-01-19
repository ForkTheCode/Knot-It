package com.forkthecode.knotit;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import models.Knot;
import utilities.CustomArrayAdapter;
import utilities.KnotitOpenHelper;
import utilities.PopulateListTask;
import utilities.tools;
import utilities.PopulateListTask.onListPopulated;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivityFragment extends Fragment implements onListPopulated {
	
	String table_name;
	public static ArrayList<Knot> mlist;
	public static CustomArrayAdapter mAdapter;
	int type;
	RelativeLayout main;
	ImageView iv;
	TextView tv;
	ProgressBar progressBar;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Boolean mFirstRun = sp.getBoolean("First_Run", true);
		if(mFirstRun){
			try {
				tools.firstRun(getActivity());
			} catch (IOException e) {
				e.printStackTrace();
			}
			 mFirstRun = false;
             sp.edit().putBoolean("First_Run", false).apply();
		}
		View view = inflater.inflate(R.layout.main_fragment,null);
		Bundle b = getArguments();
		table_name = b.getString("table_name");
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    	main = (RelativeLayout) view.findViewById(R.id.main);
		iv = (ImageView)view.findViewById(R.id.image);
		tv = (TextView)view.findViewById(R.id.text);
        switch (table_name) {
            case KnotitOpenHelper.KNOTS_TABLE_NAME:
                type = 1;
                break;
            case KnotitOpenHelper.ARCHIVED_KNOTS_TABLE_NAME:
                type = 2;
                break;
            case KnotitOpenHelper.TRASH_KNOTS_TABLE_NAME:
                type = 3;
                break;
        }
		return view;
	}



    @Override
	public void onResume() {
		super.onResume();

		main.setBackgroundColor(getResources().getColor(R.color.background_material_light));

		mlist.clear();
		listView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		if(isAdded()){
		PopulateListTask task = new PopulateListTask(getActivity(),this);
		task.execute(table_name);
		task.delegate = this;
		}

	}
	ListView listView;
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		listView = (ListView)view.findViewById(R.id.main_listView);
		mlist = new ArrayList<Knot>();
		mlist.clear();
		listView.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		if(isAdded()){
		PopulateListTask task = new PopulateListTask(getActivity(),this);
		task.execute(table_name);
		task.delegate = this;
		}
        mAdapter = new CustomArrayAdapter(getActivity(), 0 ,mlist);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Knot knot = (Knot) listView.getItemAtPosition(position);
				Intent detail = new Intent();
				detail.putExtra("type", type);
				detail.putExtra("title", knot.title);
				detail.putExtra("description", knot.description);
				detail.putExtra("image_path", knot.imageSource);
				detail.putExtra("timestamp", knot.timestamp);
				detail.putExtra("reminder_timestamp", knot.reminderTimestamp);
                detail.putExtra("isRepeating",knot.isRepeating);
                detail.putExtra("repeating_time",knot.repeatingTime);
				detail.setClass(getActivity(), DetailView.class);
				startActivity(detail);

			}
		});
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Knot knot = (Knot) listView.getItemAtPosition(position);
				if(type == 1){
					final CharSequence[] items = {getString(R.string.view),getString(R.string.edit),
                            getString(R.string.move_to_archive)
                            , getString(R.string.move_to_trash),getString(R.string.delete_knot)};
			        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setItems(items, new DialogInterface.OnClickListener() {
			        	@Override
			            public void onClick(DialogInterface dialog, int item) {
			                if (items[item].equals(getString(R.string.view))) {
			                	Intent detail = new Intent();
			    				detail.putExtra("type", type);
			    				detail.putExtra("title", knot.title);
			    				detail.putExtra("description", knot.description);
			    				detail.putExtra("image_path", knot.imageSource);
			    				detail.putExtra("timestamp", knot.timestamp);
			    				detail.putExtra("reminder_timestamp", knot.reminderTimestamp);
                                detail.putExtra("isRepeating",knot.isRepeating);
                                detail.putExtra("repeating_time",knot.repeatingTime);
			    				detail.setClass(getActivity().getApplication(), DetailView.class);
			    				startActivity(detail);
			                    dialog.dismiss();
			                } else if (items[item].equals(getString(R.string.edit))) {
			                	Intent detail = new Intent();
			    				detail.putExtra("type", 2);
			    				detail.putExtra("title", knot.title);
			    				detail.putExtra("description", knot.description);
			    				detail.putExtra("image_path", knot.imageSource);
			    				detail.putExtra("timestamp", knot.timestamp);
			    				detail.putExtra("reminder_timestamp", knot.reminderTimestamp);
                                detail.putExtra("isRepeating",knot.isRepeating);
                                detail.putExtra("repeating_time",knot.repeatingTime);
			    				detail.setClass(getActivity().getApplication(), AddNew.class);
			    				startActivity(detail);
			                    dialog.dismiss();
			                }
			                else if(items[item].equals(getString(R.string.move_to_archive))){
			                	tools.archive(getActivity().getApplication(), knot);
			                	Toast t = Toast.makeText(getActivity().getApplication(),
                                        getString(R.string.Done_for_now), Toast.LENGTH_SHORT);
			                	t.show();
			                	dialog.dismiss();
			                	getActivity().recreate();
			                }
			                else if(items[item].equals(getString(R.string.move_to_trash))){
			                	tools.moveToTrash(getActivity().getApplication(), knot);
			        			Toast t = Toast.makeText(getActivity().getApplication(),
                                        getString(R.string.moved_to_trash), Toast.LENGTH_SHORT);
			        			t.show();
			        			dialog.dismiss();
			        			getActivity().recreate();
			                }
			                else if(items[item].equals(getString(R.string.delete_knot))){
			                	dialog.dismiss();
			        			AlertDialog.Builder builder = new AlertDialog
                                        .Builder(getActivity());
			        	        builder.setMessage(getString(R.string.delete_forever_question))
			        	               .setPositiveButton(getString(R.string.delete),
                                               new DialogInterface.OnClickListener() {
			        	                   public void onClick(DialogInterface dialog, int id) {
			        	                	   tools.moveToTrash(getActivity(), knot);
			        	                       tools.permDelt(getActivity(), knot);
			        	                       if(knot.imageSource != null){
			        		                       if(knot.imageSource
                                                           .contains("com.forkthecode.knotit")){
                                                       //Delete image only if app created it
			        			                       File file = new File(knot.imageSource);
			        			                       file.delete();
			        		                       }
			        	                       }
			        	                       dialog.dismiss();
			        	                       Toast t = Toast.makeText(getActivity(),
                                                       getString(R.string.deleted_forever), Toast.LENGTH_SHORT);
			        	                       t.show();

			        	       				getActivity().recreate();

			        	                   }
			        	               })
			        	               .setNegativeButton(getString(R.string.cancel),
                                               new DialogInterface.OnClickListener() {
			        	                   public void onClick(DialogInterface dialog, int id) {
			        	                       dialog.dismiss();
			        	                   }
			        	               });
			        	        builder.show();
			                }
			            }
			        });
			        builder.show();
				}
				else if(type == 2){
					final CharSequence[] items = { getString(R.string.view) ,
                            getString(R.string.move_to_knots)
                            , getString(R.string.move_to_trash), getString(R.string.delete_knot)};
			        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setItems(items, new DialogInterface.OnClickListener() {
			        	@Override
			            public void onClick(DialogInterface dialog, int item) {
			                if (items[item].equals(getString(R.string.view))) {
			                	Intent detail = new Intent();
			    				detail.putExtra("type", type);
			    				detail.putExtra("title", knot.title);
			    				detail.putExtra("description", knot.description);
			    				detail.putExtra("image_path", knot.imageSource);
			    				detail.putExtra("timestamp", knot.timestamp);
			    				detail.putExtra("reminder_timestamp", knot.reminderTimestamp);
                                detail.putExtra("isRepeating",knot.isRepeating);
                                detail.putExtra("repeating_time",knot.repeatingTime);
			    				detail.setClass(getActivity().getApplication(), DetailView.class);
			    				startActivity(detail);
			                    dialog.dismiss();
			                }else if(items[item].equals(getString(R.string.move_to_knots) )){
			                	tools.unArchive(getActivity().getApplication(), knot);
			                	Toast t = Toast.makeText(getActivity(),
                                        getString(R.string.moved_to_Knots), Toast.LENGTH_SHORT);
			        			t.show();
			                	dialog.dismiss();
			                	getActivity().recreate();
			                }
			                else if(items[item].equals(getString(R.string.move_to_trash))){
			                	tools.moveToTrashFromArchived(getActivity().getApplication(), knot);
			        			Toast t = Toast.makeText(getActivity(), getString(R.string.moved_to_trash),
                                        Toast.LENGTH_SHORT);
			        			t.show();
			        			dialog.dismiss();
			        			getActivity().recreate();
			                }
			                else if(items[item].equals(getString(R.string.delete_knot))){
			                	dialog.dismiss();
			        			AlertDialog.Builder builder = new AlertDialog
                                        .Builder(getActivity());
			        	        builder.setMessage(getString(R.string.delete_forever_question))
			        	               .setPositiveButton(getString(R.string.delete),
                                               new DialogInterface.OnClickListener() {
			        	                   public void onClick(DialogInterface dialog, int id) {
			        	                	   tools.moveToTrashFromArchived(getActivity(), knot);
			        	                       tools.permDelt(getActivity(), knot);
			        	                       if(knot.imageSource != null){
			        		                       if(knot.imageSource
                                                           .contains("com.forkthecode.knotit" )){
			        			                       File file = new File(knot.imageSource);
			        			                       file.delete();
			        		                       }
			        	                       }
			        	                       dialog.dismiss();
			        	                       Toast t = Toast.makeText(getActivity(),
                                                       getString(R.string.deleted_forever), Toast.LENGTH_SHORT);
			        	                       t.show();

			        	       				getActivity().recreate();

			        	                   }
			        	               })
			        	               .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
			        	                   public void onClick(DialogInterface dialog, int id) {
			        	                       dialog.dismiss();
			        	                   }
			        	               });
			        	        builder.show();
			                }
			            }
			        });
			        builder.show();
				}
				else if(type == 3){
					final CharSequence[] items = { getString(R.string.view) ,
                            getString(R.string.move_to_knots)
                            , getString(R.string.move_to_archive) , getString(R.string.delete_knot)};
			        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			        builder.setItems(items, new DialogInterface.OnClickListener() {
			        	@Override
			            public void onClick(DialogInterface dialog, int item) {
			                if (items[item].equals(getString(R.string.view))) {
			                	Intent detail = new Intent();
			    				detail.putExtra("type", type);
			    				detail.putExtra("title", knot.title);
			    				detail.putExtra("description", knot.description);
			    				detail.putExtra("image_path", knot.imageSource);
			    				detail.putExtra("timestamp", knot.timestamp);
                                detail.putExtra("isRepeating",knot.isRepeating);
                                detail.putExtra("repeating_time",knot.repeatingTime);
			    				detail.putExtra("reminder_timestamp", knot.reminderTimestamp);
			    				detail.setClass(getActivity().getApplication(), DetailView.class);
			    				startActivity(detail);
			                    dialog.dismiss();
			                }else if(items[item].equals(getString(R.string.move_to_knots))){
			                	tools.moveFromTrashToMain(getActivity(), knot);
			                	Toast t = Toast.makeText(getActivity(), getString(R.string.moved_to_Knots),
                                        Toast.LENGTH_SHORT);
			        			t.show();
			                	dialog.dismiss();
			                	getActivity().recreate();
			                }
			                else if(items[item].equals(getString(R.string.move_to_archive))){
			                	tools.moveFromTrashToArchived(getActivity(), knot);
			        			Toast t = Toast.makeText(getActivity(),getString(R.string.Done_for_now),
                                        Toast.LENGTH_SHORT);
			        			t.show();
			        			dialog.dismiss();
			        			getActivity().recreate();
			                }
			                else if(items[item].equals(getString(R.string.delete_knot))){
			                	dialog.dismiss();
			        			AlertDialog.Builder builder = new AlertDialog
                                        .Builder(getActivity());
			        	        builder.setMessage(getString(R.string.delete_forever_question))
			        	               .setPositiveButton(getString(R.string.delete),
                                               new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int id) {
                                               tools.permDelt(getActivity(), knot);
                                               if (knot.imageSource != null) {
                                                   if (knot.imageSource
                                                           .contains("com.forkthecode.knotit")) {
                                                       File file = new File(knot.imageSource);
                                                       file.delete();
                                                   }
                                               }
                                               dialog.dismiss();
                                               Toast t = Toast.makeText(getActivity(),
                                                       getString(R.string.deleted_forever), Toast.LENGTH_SHORT);
                                               t.show();

                                               getActivity().recreate();

                                           }
                                       })
			        	               .setNegativeButton(getString(R.string.cancel),
                                               new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int id) {
                                               dialog.dismiss();
                                           }
                                       });
			        	        builder.show();
			                }
			            }
			        });
			        builder.show();
				}
				return true;
			}
		});
	}

	@Override
	public void setAdapter(ArrayList<Knot> list) {
		main.setBackgroundColor(getResources().getColor(R.color.background_material_light));
		mlist.clear();
		mlist.addAll(list);
		listView.setVisibility(View.VISIBLE);
		mAdapter.notifyDataSetChanged();
		progressBar.setVisibility(View.GONE);
		if(list.isEmpty()){
			main.setBackgroundColor(getResources().getColor(R.color.background_material_dark));
			iv.setVisibility(View.VISIBLE);
			tv.setVisibility(View.VISIBLE);
		}
		else{
			main.setBackgroundColor(getResources().getColor(R.color.background_material_light));
			iv.setVisibility(View.GONE);
			tv.setVisibility(View.GONE);
		}
		
	}
}
