/*
 * Copyright (C) 2016 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.shubham.learner;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DonateActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    private static final String DONATION_1 = "naman14.algo.donate_1";
    private static final String DONATION_2 = "naman14.algo.donate_2";
    private static final String DONATION_3 = "naman14.algo.donate_3";
    private static final String DONATION_5 = "naman14.algo.donate_5";
    private static final String DONATION_10 = "naman14.algo.donate_10";


    private boolean readyToPurchase = false;
    BillingProcessor bp;

    private LinearLayout productListView;
    private ProgressBar progressBar;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Donate");
        productListView = (LinearLayout) findViewById(R.id.product_list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        status = (TextView) findViewById(R.id.donation_status);

        bp = new BillingProcessor(this, getString(R.string.app_name), this);

    }

    @Override
    public void onBillingInitialized() {
        readyToPurchase = true;
        checkStatus();
        getProducts();
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        checkStatus();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DonateActivity.this, "Thanks for your donation!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DonateActivity.this, "Unable to process purchase", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkStatus() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                List<String> owned = bp.listOwnedProducts();
                return owned != null && owned.size() != 0;
            }

            @Override
            protected void onPostExecute(Boolean b) {
                super.onPostExecute(b);
                if (b) {
                    status.setText("Thanks for your donation!");
                }
            }
        }.execute();
    }

    private void getProducts() {

        new AsyncTask<Void, Void, List<SkuDetails>>() {
            @Override
            protected List<SkuDetails> doInBackground(Void... voids) {

                ArrayList<String> products = new ArrayList<>();

                products.add(DONATION_1);
                products.add(DONATION_2);
                products.add(DONATION_3);
                products.add(DONATION_5);
                products.add(DONATION_10);

                return bp.getPurchaseListingDetails(products);
            }

            @Override
            protected void onPostExecute(List<SkuDetails> productList) {
                super.onPostExecute(productList);

                if (productList == null)
                    return;

                Collections.sort(productList, new Comparator<SkuDetails>() {
                    @Override
                    public int compare(SkuDetails skuDetails, SkuDetails t1) {
                        if (skuDetails.priceValue >= t1.priceValue)
                            return 1;
                        else if (skuDetails.priceValue <= t1.priceValue)
                            return -1;
                        else return 0;
                    }
                });
                for (int i = 0; i < productList.size(); i++) {
                    final SkuDetails product = productList.get(i);
                    View rootView = LayoutInflater.from(DonateActivity.this).inflate(R.layout.item_donate_product, productListView, false);

                    TextView detail = (TextView) rootView.findViewById(R.id.product_detail);
                    detail.setText(product.priceText);

                    rootView.findViewById(R.id.btn_donate).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (readyToPurchase)
                                bp.purchase(DonateActivity.this, product.productId);
                            else
                                Toast.makeText(DonateActivity.this, "Unable to initiate purchase", Toast.LENGTH_SHORT).show();
                        }
                    });

                    productListView.addView(rootView);

                }
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
