package ywb.testcity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.zhy.autolayout.AutoLayoutActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AutoLayoutActivity implements CitySlideView.OnSelectedLettleListener{
	private RecyclerView rv;
	private LinearLayoutManager llManager;
	private PinyinComparator pinyinComparator;
	private TextView tvStickTitle,tvSelectedShow;
	private CityAdapter cityAdapter;
	private CitySlideView mCitySlideView;
	private List<City> cityList = new ArrayList<>();
	public static List<String> letterList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CrashHandler.getInstance().init(getApplicationContext());

		rv = (RecyclerView) findViewById(R.id.recyclerView);
		llManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
		rv.setLayoutManager(llManager);
		pinyinComparator = new PinyinComparator();
		tvStickTitle = (TextView) findViewById(R.id.stick_tv);
		tvSelectedShow = (TextView) findViewById(R.id.tv_selected_show);
		mCitySlideView = (CitySlideView) findViewById(R.id.view_city_slide);
		mCitySlideView.setOnSelectedLettleListener(this);

		for (int i = 0; i < City.cityArray.length; i++) {
			City city = new City();
			city.name = City.cityArray[i];
			city.pinyin = transformPinYin(City.cityArray[i]);
			city.pinyinFir = city.pinyin.substring(0,1);
			cityList.add(city);
		}

		Collections.sort(cityList,pinyinComparator);

		Set<String> letterSet = new LinkedHashSet<>();
		for (City city:cityList) {
			letterSet.add(city.pinyinFir);
		}

		for (String letter:letterSet) {
			letterList.add(letter);
		}

		cityAdapter = new CityAdapter(this,cityList);
		rv.setAdapter(cityAdapter);


		rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);

				View visibleFirView = recyclerView.findChildViewUnder(tvStickTitle.getMeasuredWidth() / 2,5);
				if(visibleFirView != null && visibleFirView.getContentDescription() != null){
					tvStickTitle.setText(visibleFirView.getContentDescription());
				}

				View childView = recyclerView.findChildViewUnder(tvStickTitle.getMeasuredWidth() / 2,tvStickTitle.getMeasuredHeight() + 1);
				if(childView != null && childView.getTag() != null){

					int state = (int) childView.getTag();
					int scrollY = childView.getTop() - tvStickTitle.getMeasuredHeight();
					if(state == CityAdapter.HAS_STICK){

						if(childView.getTop() <= 0){
							tvStickTitle.setTranslationY(0);
						}else{
							tvStickTitle.setTranslationY(scrollY);
						}
					}else{
						tvStickTitle.setTranslationY(0);
					}
				}

			}
		});

	}

	public String transformPinYin(String character) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < character.length(); i++) {
			buffer.append(Pinyin.toPinyin(character.charAt(i)));
		}
		return buffer.toString();
	}

	@Override
	public void onSelected(String lettle) {
		tvSelectedShow.setVisibility(View.VISIBLE);
		tvSelectedShow.setText(lettle);

		for (int i = 0; i < cityList.size(); i++) {
			if(cityList.get(i).pinyinFir.equals(lettle)){
				rv.scrollToPosition(i);
				return;
			}
		}

	}

	@Override
	public void onUp(){
		tvSelectedShow.setVisibility(View.GONE);
	}

	public class PinyinComparator implements Comparator<City> {
		@Override
		public int compare(City cityFirst, City citySecond) {
			return cityFirst.pinyin.compareTo(citySecond.pinyin);
		}
	}
}
