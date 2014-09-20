/* Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.adp.lumus.general;

import android.util.Log;

import com.example.android.util.IabHelper;
import com.example.android.util.IabResult;
import com.example.android.util.Inventory;
import com.example.android.util.Purchase;

public class CompraPacote {
	// Debug tag, for logging
	static final String TAG = "Lumus";
	private ResourcesManager rm;


	// SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
	static final String SKU_PACK1 = "pack1";
	static final String SKU_PACK2 = "pack2";
	static final String SKU_PACK3 = "pack3";

	private boolean hasPack1 ;
	private boolean hasPack2 ;
	private boolean hasPack3 ;

	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 66315;

	// The helper object
	private IabHelper mHelper;

	public CompraPacote(ResourcesManager rmanager) {
		/* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
		 * (that you got from the Google Play developer console). This is not your
		 * developer public key, it's the *app-specific* public key.
		 *
		 * Instead of just storing the entire literal string here embedded in the
		 * program,  construct the key at runtime from pieces or
		 * use bit manipulation (for example, XOR with some other string) to hide
		 * the actual key.  The key itself is not secret information, but we don't
		 * want to make it easy for an attacker to replace the public key with one
		 * of their own and then fake messages from the server.
		 */
		String base64EncodedPublicKey = fazerChave();

		this.rm = rmanager;

		// Create the helper, passing it our context and the public key to verify signatures with
		mHelper = new IabHelper(rm.activity, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set this to false).
		mHelper.enableDebugLogging(false);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});

		rm.activity.setIabHelper(mHelper);
	}

	// Listener that's called when we finish querying the items and subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			// Do we have pack1?
			Purchase pack1Purchase = inventory.getPurchase(SKU_PACK1);
			hasPack1 = (pack1Purchase != null && verifyDeveloperPayload(pack1Purchase));

			// Do we have pack2?
			Purchase pack2Purchase = inventory.getPurchase(SKU_PACK2);
			hasPack2 = (pack2Purchase != null && verifyDeveloperPayload(pack2Purchase));

			// Do we have pack3?
			Purchase pack3Purchase = inventory.getPurchase(SKU_PACK3);
			hasPack3 = (pack3Purchase != null && verifyDeveloperPayload(pack3Purchase));
		}
	};

	// User clicked the "Buy Gas" button
	public void onBuyPacoteButtonClicked(int pack) {
		Log.d(TAG, "Buy Pacote button clicked.");

		switch(pack){
		case 1:
			if (hasPack1) {
				rm.preferences.edit().putBoolean("PAID_1", true).commit();
				complain("No need! You already have this pack!");
				return;
			}
			break;
		case 2:
			if (hasPack2) {
				rm.preferences.edit().putBoolean("PAID_2", true).commit();
				complain("No need! You already have this pack!");
				return;
			}
			break;
		case 3:
			if (hasPack3) {
				rm.preferences.edit().putBoolean("PAID_3", true).commit();
				complain("No need! You already have this pack!");
				return;
			}
			break;
		default: break;
		}

		// launch the gas purchase UI flow.
		// We will be notified of completion via mPurchaseFinishedListener
		Log.d(TAG, "Launching purchase flow for pack" + pack);

		/* TODO: for security, generate your payload here for verification. See the comments on 
		 *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use 
		 *        an empty string, but on a production app you should carefully generate this. */
		String payload = ""; 

		switch(pack){
		case 1:
			mHelper.launchPurchaseFlow(rm.activity, SKU_PACK1, RC_REQUEST, mPurchaseFinishedListener, payload);
			break;
		case 2:
			mHelper.launchPurchaseFlow(rm.activity, SKU_PACK2, RC_REQUEST, mPurchaseFinishedListener, payload);
			break;
		case 3:
			mHelper.launchPurchaseFlow(rm.activity, SKU_PACK3, RC_REQUEST, mPurchaseFinishedListener, payload);
			break;
		default: break;
		}

	}


	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		//String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct. It will be
		 * the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase and 
		 * verifying it here might seem like a good approach, but this will fail in the 
		 * case where the user purchases an item on one device and then uses your app on 
		 * a different device, because on the other device you will not have access to the
		 * random string you originally generated.
		 *
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different between them,
		 *    so that one user's purchase can't be replayed to another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app wasn't the
		 *    one who initiated the purchase flow (so that items purchased by the user on 
		 *    one device work on other devices owned by the user).
		 * 
		 * Using your own server to store and verify developer payloads across app
		 * installations is recommended.
		 */

		return true;
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
			if (result.isFailure()) {
				complain("Error purchasing: " + result); 
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				return;
			}

			Log.d(TAG, "Purchase successful.");

			if (purchase.getSku().equals(SKU_PACK1)) {
				Log.d(TAG, "Purchase is PACK1. Starting consumption.");
				//mHelper.consumeAsync(purchase, mConsumeFinishedListener);
				rm.preferences.edit().putBoolean("PAID_1", true).commit();
			}
			else if (purchase.getSku().equals(SKU_PACK2)) {
				Log.d(TAG, "Purchase is PACK2. Starting consumption.");
				//mHelper.consumeAsync(purchase, mConsumeFinishedListener);
				rm.preferences.edit().putBoolean("PAID_2", true).commit();
			}
			else if (purchase.getSku().equals(SKU_PACK2)) {
				Log.d(TAG, "Purchase is PACK3. Starting consumption.");
				//mHelper.consumeAsync(purchase, mConsumeFinishedListener);
				rm.preferences.edit().putBoolean("PAID_3", true).commit();
			}
		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		@Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

			// We know this is the "gas" sku because it's the only one we consume,
			// so we don't check which sku was consumed. If you have more than one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in our
				// game world's logic, which in our case means filling the gas tank a bit
				Log.d(TAG, "Consumption successful. Provisioning.");
			}
			else {
				complain("Error while consuming: " + result);
			}
			Log.d(TAG, "End consumption flow.");
		}
	};

	// We're being destroyed. It's important to dispose of the helper here!
	public void destroy() {       
		// very important:
		Log.d(TAG, "Destroying helper.");
		if (mHelper != null) mHelper.dispose();
		mHelper = null;
	}

	void complain(String message) {
		Log.e(TAG, "**** TrivialDrive Error: " + message);
	}


	public boolean isHasPack1() {return hasPack1;}
	public boolean isHasPack2() {return hasPack2;}
	public boolean isHasPack3() {return hasPack3;}
}