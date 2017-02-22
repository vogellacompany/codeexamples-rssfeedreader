package com.example.android.rssreader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.Toolbar;

public class RssfeedActivity extends Activity implements
        MyListFragment.OnItemSelectedListener {

    SelectionStateFragment stateFragment;
    Toolbar tb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tb = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(tb);
        stateFragment =
                (SelectionStateFragment) getFragmentManager()
                        .findFragmentByTag("headless");

        if(stateFragment == null) {
            stateFragment = new SelectionStateFragment();
            getFragmentManager().beginTransaction()
                    .add(stateFragment, "headless").commit();
        }

        if (getResources().getBoolean(R.bool.twoPaneMode)) {
            // restore state
            if (stateFragment.lastSelection.length()>0) {
                onRssItemSelected(stateFragment.lastSelection);
            }
            // otherwise all is good, we use the fragments defined in the layout
            return;
        }
        // if savedInstanceState is null we do some cleanup
        if (savedInstanceState != null) {
            // cleanup any existing fragments in case we are in detailed mode #<1>
            getFragmentManager().executePendingTransactions();
            Fragment fragmentById = getFragmentManager().
                    findFragmentById(R.id.fragment_container);
            if (fragmentById!=null) {
                getFragmentManager().beginTransaction()
                        .remove(fragmentById).commit();
            }
        }

        MyListFragment listFragment = new MyListFragment();
        FrameLayout viewById = (FrameLayout) findViewById(R.id.fragment_container);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, listFragment).commit();

    }

    @Override
    public void onRssItemSelected(String link) {

        stateFragment.lastSelection = link;
        if (getResources().getBoolean(R.bool.twoPaneMode)) {
            DetailFragment fragment = (DetailFragment) getFragmentManager()
                    .findFragmentById(R.id.detailFragment);
            fragment.setText(link);

        } else {
            // replace the fragment
            // Create fragment and give it an argument for the selected article
            DetailFragment newFragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putString(DetailFragment.EXTRA_URL, link);
            newFragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back

            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

        }
    }

    @Override
    public void toolbarAnimateShow() {
        tb.animate()
                .translationY(0)
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        tb.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void toolbarAnimateHide() {
        tb.animate()
                .translationY(tb.getHeight())
                .setInterpolator(new LinearInterpolator())
                .setDuration(180)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                       tb.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        tb.inflateMenu(R.menu.mainmenu);
        tb.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if (getResources().getBoolean(R.bool.twoPaneMode)) {
                    MyListFragment fragment = (MyListFragment) getFragmentManager().findFragmentById(R.id.listFragment);
                    fragment.updateListContent();
                } else {
                    Fragment fragmentById = getFragmentManager().findFragmentById(R.id.fragment_container);
                    if (fragmentById instanceof MyListFragment) {
                        MyListFragment fragment = (MyListFragment) fragmentById;
                        fragment.updateListContent();
                    }
                }

                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Action Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_network:
                Intent wirelessIntent = new Intent("android.settings.WIRELESS_SETTINGS");
                startActivity(wirelessIntent);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
