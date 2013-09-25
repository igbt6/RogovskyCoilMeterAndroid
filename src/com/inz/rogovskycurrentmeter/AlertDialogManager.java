package com.inz.rogovskycurrentmeter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class AlertDialogManager {

	private Context _context;

	public AlertDialogManager(Context context) { // konstruktor
		_context = context;
	}

	/**
	 * Function to display simple Alert Dialog
	 * 
	 * @
	 * 
	 * @param title
	 *            - alert dialog title
	 * @param message
	 *            - alert message
	 * @param status
	 *            - a sort of the massage: 0-failure, 1-ok_success, 2-alert
	 * */
	public void showAlertDialog(String title, String message, Integer status /*
																			 * 
																			 * Boolean
																			 * status
																			 */) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				_context);

		alertDialogBuilder.setTitle(title).setMessage(message)
				.setCancelable(true).setInverseBackgroundForced(true);

		switch (status) {

		case 0: {
			alertDialogBuilder
					.setIcon(R.drawable.ic_ok)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// /tutaj cos tam jeszcze bnie wiem ALE SIIE
									// DOWIEM
									Toast.makeText(
											_context.getApplicationContext(),
											"WIFI HAS BEEN ENABLED",
											Toast.LENGTH_SHORT).show();

									dialog.cancel();

								}
							})

					.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});

			break;
		}
		case 1: {
			alertDialogBuilder
					.setIcon(R.drawable.ic_not_ok)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// / tutaj pytanie czy wlaczyc bluettoth
									Toast.makeText(
											_context.getApplicationContext(),
											"WIFI HAS BEEN ENABLED",
											Toast.LENGTH_SHORT).show();

									dialog.cancel();

								}
							})

					.setNegativeButton("NO",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							});

			break;
		}

		case 2: {
			alertDialogBuilder.setIcon(R.drawable.ic_alert)

			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			break;
		}
		default:
			break;
		}

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}
}