package br.com.gigaspace.contas.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.gigaspace.contas.R;
import br.com.gigaspace.contas.model.Account;
import br.com.gigaspace.contas.model.AccountTypeEnum;

import java.util.ArrayList;

public class AccountsListAdapter extends BaseAdapter {
	private ArrayList<Account> listData;
	private LayoutInflater layoutInflater;
	private Context context;

	public AccountsListAdapter(Context context, ArrayList<Account> listData) {
		this.listData = listData;
		this.context = context;
		this.layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class AccountViewHolder {
		TextView nameView;
		TextView accountTypeView;
		TextView accountValueView;
		ImageView thumbImageView;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		AccountViewHolder holder;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_row_layout_accounts, null);
			holder = new AccountViewHolder();
			holder.nameView = (TextView) convertView.findViewById(R.id.title);
			holder.accountTypeView = (TextView) convertView.findViewById(R.id.type);
			holder.accountValueView = (TextView) convertView.findViewById(R.id.value);
			holder.thumbImageView = (ImageView) convertView.findViewById(R.id.list_image);
			convertView.setTag(holder);
		}
		else {
			holder = (AccountViewHolder) convertView.getTag();
		}
		
		holder.nameView.setText(listData.get(position).getNomeConta());
		holder.accountTypeView.setText(AccountTypeEnum.getById(listData.get(position).getTipoConta()).getDescricao());
		holder.accountValueView.setText(listData.get(position).getValorAtualAsString());

		Integer tipoConta = listData.get(position).getTipoConta();

		if (tipoConta.intValue() == AccountTypeEnum.CARTAO_CREDITO.getId().intValue()) {
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_credit_cards);
			holder.thumbImageView.setImageBitmap(bm);
		}
		else if (tipoConta.intValue() == AccountTypeEnum.POUPANCA.getId().intValue()) {
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_piggy);
			holder.thumbImageView.setImageBitmap(bm);
		}
		else {
			Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_money);
			holder.thumbImageView.setImageBitmap(bm);
		}

		return convertView;
	}

}
