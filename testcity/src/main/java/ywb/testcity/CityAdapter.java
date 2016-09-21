package ywb.testcity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Johnny on 2016/8/15.
 */
public class CityAdapter extends RecyclerView.Adapter{

	public static final int FIRST_STICK = 1;
	public static final int HAS_STICK = 2;
	public static final int NONE_STICK = 3;

	private Context context;
	private List<City> cityList;
	private LayoutInflater inflater;

	public CityAdapter(Context context,List<City> list){
		this.context = context;
		this.cityList = list;
		inflater = LayoutInflater.from(context);
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.item,parent,false);
		return new ItemHolder(view);
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
		if(holder instanceof ItemHolder){
			ItemHolder itemHolder = (ItemHolder) holder;

			City city = cityList.get(position);
			((ItemHolder) holder).tvName.setText(city.name);
			((ItemHolder) holder).tvPos.setText("+ " + position);

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context,"点击了" + holder.getLayoutPosition(),Toast.LENGTH_SHORT).show();
				}
			});

			if(position == 0){
				itemHolder.tvHeader.setVisibility(View.VISIBLE);
				itemHolder.tvHeader.setText(city.pinyinFir);
				itemHolder.itemView.setTag(FIRST_STICK);
			}else{
				if(city.pinyinFir.equals(cityList.get(position - 1).pinyinFir)){
					itemHolder.tvHeader.setVisibility(View.GONE);
					itemHolder.itemView.setTag(NONE_STICK);
				}else{
					itemHolder.tvHeader.setVisibility(View.VISIBLE);
					itemHolder.tvHeader.setText(city.pinyinFir);
					itemHolder.itemView.setTag(HAS_STICK);
				}
			}

			itemHolder.itemView.setContentDescription(city.pinyinFir);

//			if (position == 0) {
//				itemHolder.tvHeader.setVisibility(View.VISIBLE);
//				itemHolder.tvHeader.setText(city.pinyinFir);
//				itemHolder.itemView.setTag(FIRST_STICKY_VIEW);
//			} else {
//				if (!TextUtils.equals(city.pinyinFir, cityList.get(position - 1).pinyinFir)) {
//					itemHolder.tvHeader.setVisibility(View.VISIBLE);
//					itemHolder.tvHeader.setText(city.pinyinFir);
//					itemHolder.itemView.setTag(HAS_STICKY_VIEW);
//				} else {
//					itemHolder.tvHeader.setVisibility(View.GONE);
//					itemHolder.itemView.setTag(NONE_STICKY_VIEW);
//				}
//			}
//
//			itemHolder.itemView.setContentDescription(city.pinyinFir);

		}
	}

	@Override
	public int getItemCount() {
		return cityList.size();
	}

	private class ItemHolder extends RecyclerView.ViewHolder{
		private TextView tvName,tvPos;
		private TextView tvHeader;
		public ItemHolder(View itemView) {
			super(itemView);
			tvName = (TextView) itemView.findViewById(R.id.tv_item_name);
			tvPos = (TextView) itemView.findViewById(R.id.tv_item_pos);
			tvHeader = (TextView) itemView.findViewById(R.id.stick_tv);
		}
	}

}
