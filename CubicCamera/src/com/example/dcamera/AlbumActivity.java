package com.example.dcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AlbumActivity extends Activity {

	MainActivity mainAct ;
	
	private int checkCnt ;	// üũ�� ����
	private boolean[] checking ;	// �� �� ������ üũ ����
	private ArrayList<Bitmap> img ;	// �̹��� ����Ʈ
	private LinearLayout linearLay ;	// �޴� ���̾ƿ�
	private Toast toast =null ;	// toast �ߺ� ����	
	private GridView gridView ;	// �׸��� ��

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.album) ;

		linearLay =(LinearLayout) findViewById (R.id.album_menu) ;
		gridView =(GridView) findViewById (R.id.album_gridView) ;
		GridAdapter gridAdapter =new GridAdapter (this) ;
		gridView.setAdapter(gridAdapter) ;	// �׸��� �信 ����� ��
		
		// ������ ���� ���� ���
		gridView.setOnItemClickListener(new OnItemClickListener () {
			public void onItemClick (AdapterView <?> parent, View view, int position, long id) {
				// AutoItemActivity�� ���� �� ������ ������ ����
				Intent item =new Intent (AlbumActivity.this, AutoItemActivity.class) ;
				item.putExtra("item", (position +1)) ;
				startActivity (item) ;
			}
		}) ;
		gridView.setOnScrollListener(new OnScrollListener () {
			public void onScrollStateChanged (AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE :	// ����
					break ;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL :	// ��ġ
					break ;
				case OnScrollListener.SCROLL_STATE_FLING :	// �̵�
					break ;
				}
			}
			
			public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
			}
		}) ;
	}
	
	// ����� Ŭ����
	public class GridAdapter extends BaseAdapter {
		
		private Context context ;
		private LayoutInflater inflater ;

		public GridAdapter (Context context) {
			this.context =context ;
			this.inflater =getLayoutInflater() ;
		}

		// ���� ����
		public int getCount() {
			return (null != img) ? img.size () : 0 ;
		}

		// ���� ID
		public Object getItem(int position) {
			return (null != img) ? img.get(position) : 0 ;
		}

		// ���� position
		public long getItemId(int position) {
			return position ;
		}

		// �� ��
		public View getView(int position, View convertView, ViewGroup parent) {
		
				/*ImageView imgView =null ;
				if (null == convertView) {
					imgView = new ImageView(context);
					imgView.setLayoutParams(new GridView.LayoutParams(
							(int) (mainAct.scaleWidth / 2) - 25,
							(int) (mainAct.scaleWidth / 2) - 25));
					imgView.setAdjustViewBounds(true);
					imgView.setScaleType(ScaleType.CENTER_CROP);
				} else {
					Log.d ("D", "A") ;
					imgView = (ImageView) convertView;
				}
				imgView.setImageBitmap(img.get(position));

				return imgView;*/

			// convertView�� inflate ���� �Ѵ�.
			convertView = inflater.inflate(R.layout.album_checkbox, null);
			ImageView imgView = (ImageView) convertView.findViewById(R.id.check_img);
			CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.check_checkbox);
			checkBox.setTag(position) ;	// �� üũ�ڽ��� ���� �ڱ� ���� position ��
			
			// �޴� ���̾ƿ��� �Ⱥ��� �� üũ �ڽ��� �Ⱥ��̰�
			if (linearLay.getVisibility() == View.GONE)
				checkBox.setVisibility(View.GONE);
			// �޴� ���̾ƿ��� ���̸� üũ�ڽ��� ���̰�
			else {
				checkBox.setVisibility(View.VISIBLE);
				checkBox.setChecked(checking[position]) ;	// üũ�ڽ� ���� ����
			}
			// üũ�ڽ��� üŷ�� ���� ��
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton view, boolean isChecked) {
					// üũ�ڽ� üŷ ���°� �ٲ�� �� ���� ����
					checking[(Integer)view.getTag ()] =isChecked ;
					// üũ�� ���� ���
					if (view.isChecked()) checkCnt++ ;	
					else checkCnt-- ;
				}
			});
			// ���� ũ�� �� ������ �̹��� ��
			convertView.setLayoutParams(new GridView.LayoutParams(
					(int) (mainAct.scaleWidth / 2) - 25, (int) (mainAct.scaleWidth / 2) - 25));
			imgView.setImageBitmap(img.get(position));

			return convertView;
		}
	}
	
	// �ð��� ����
	public void sortFiles(File[] files) {
		// �⺻ sort���� �ð��� ���� �߰�
		Arrays.sort(files, new Comparator<Object>() {
			public int compare(Object obj1, Object obj2) {
				String s1 = ((File) obj1).lastModified() + "";
				String s2 = ((File) obj2).lastModified() + "";

				return s1.compareTo(s2); // s1�� s2 ��
			}
		});
	}
	
	// ���� �̸� ����
	public void renameFiles(String path) {
		// �Ķ���ͷ� ���� ��η� ���� ���� �� ���� ���� Ž��
		File pathFile = new File(path);
		File[] childFiles = pathFile.listFiles();
		String dir = "/Cubic_";

		sortFiles(childFiles); // listFiles()�� �� ���� ���ϵ��� �ð� ������ ����
		// 0�κ��� .nomedia�����̹Ƿ� ����
		for (int i = 1; i < childFiles.length; i++) {
			// ������ ���� ���� Ž��
			File temp = new File(path + dir + i);
			if (!temp.exists()) { // ������ ������
				try {
					// ���ĵ� ���������� �̸��� ����
					childFiles[i].renameTo(new File(path + dir + i));
				} catch (Exception e) {
				}
			}
		}
	}
	
	// ���� ����
	public void deleteFiles(String path) {
		// �Ķ���ͷ� ���� ��η� ���� ���� �� ���� ���� Ž��
		File pathFile = new File(path);
		File[] childFiles = pathFile.listFiles();

		for (File childFile : childFiles) {
			// ���� ���ϰ� ���� �� ��� ���ȣ��� ���� ���� ���� ����
			if (childFile.isDirectory())
				deleteFile(childFile.getAbsolutePath());
			// ���� ������ ������ ����
			else
				childFile.delete();
		}
		pathFile.delete(); // ���� ���� ����
	}
	
	// delete Ŭ��
	public void deleteClick (View v) {
		// üũ�� �ϳ��� ���� ���
		if (checkCnt <= 0) {
			if (toast == null) 
				toast =Toast.makeText(this, "������ �׸��� �����ϴ�.", Toast.LENGTH_LONG) ;
			toast.show() ;
			
			return ;
		}
		// �⺻��� �� ���� ���� ��� ����
		File pathOrg = Environment.getExternalStorageDirectory();
		String dir = "/CubicCamera/Cubic_";
		
		// �� ���� ���� üũ�� �Ǿ��־����� ����
		for (int i =0 ; i < checking.length ; i++) {
			if (checking[i]) deleteFiles (pathOrg +dir +(i+1)) ;
		}
		renameFiles (pathOrg +"/CubicCamera") ;	// ������ ���ϵ��� �̸� �� ����
		onResume () ;	// �׸��� �� ����
	}
	
	// add Ŭ��
	public void addClick (View v) {
		linearLay.setVisibility(View.GONE) ;	// �޴� ���̾ƿ��� �ݰ�
		gridView.invalidateViews() ;	// �׸���� ����
		// cameraActivity Intent ����
		Intent camera =new Intent (this, CameraActivity.class) ;
		startActivity (camera) ;
	}

	// �ڷ� ���� ��ưŬ����
	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (KeyCode == KeyEvent.KEYCODE_BACK) {
				// �޴� ���̾ƿ��� ���̸� ���ش�.
				if (linearLay.getVisibility() == View.VISIBLE) {
					linearLay.setVisibility(View.GONE) ;
					for (int i =0 ; i < checking.length ; i++) checking[i] =false ;	// �ʱ�ȭ
					gridView.invalidateViews() ;	// �׸��� �� ����
					return true ;
				}
				img.removeAll(img);	// ����Ʈ �ʱ�ȭ
				finish(); // Activity�� ����

				return true;
			}
			else if (KeyCode == KeyEvent.KEYCODE_MENU) {
				// �޴� ���̾ƿ��� �Ⱥ��� ��
				if (linearLay.getVisibility() == View.GONE)
					linearLay.setVisibility(View.VISIBLE) ;	// �޴� ���̾ƿ� ����
				else {
					linearLay.setVisibility(View.GONE) ;	// �޴� ���̾ƿ� ����
					for (int i =0 ; i < checking.length ; i++) checking[i] =false ;	// �ʱ�ȭ
				}
				gridView.invalidateViews() ;	// �׸��� �� ����
				//openOptionsMenu(); 
				
				return true ;
			}
		}
		return super.onKeyDown(KeyCode, event);
	}
	
	// Activity Resume
	public void onResume () {
		super.onResume () ;
		checkCnt =0 ;	// �ʱ�ȭ
		img = new ArrayList<Bitmap>();	// �ʱ�ȭ
		// �⺻��� �� ���� ���� ��� ����
		File pathOrg = Environment.getExternalStorageDirectory();
		String dir = "/CubicCamera/Cubic_";
		int num = 1;
		// ����
		try {
			File pathApp = new File(pathOrg + dir + num); // Load���
			// if (!pathApp.isDirectory()) pathApp.mkdirs(); // ������ ����

			while (true) {
				if (pathApp.exists()) {	// ������ ������
					img.add(BitmapFactory.decodeFile(pathApp + "/Cubic_0.png"));	// Load
					pathApp = new File(pathOrg + dir + (++num));	// ���� ���� Ž��
				}
				// ������ ������ ���� Ż��
				else break;
			}
		} catch (Exception e) {
			Toast.makeText(this, "������ �ҷ����� ���� ������ �߻� �Ͽ����ϴ�.", Toast.LENGTH_LONG).show();
			finish();
		}
		checking = new boolean[img.size()];	// ���� ������ŭ �� �Ҵ�
		gridView.invalidateViews() ;	// �׸��� �� ����
	}
}
