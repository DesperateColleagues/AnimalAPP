package it.uniba.dib.sms22235.tasks.common.views.reports;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms22235.R;
import it.uniba.dib.sms22235.tasks.NavigationActivityInterface;

/**
 * This fragment is used to display the ViewPager which holds the list
 * of community and personal reports
 * */
public class ReportsDashboardFragment extends Fragment implements
        ReportsListFragment.ManageNavigationReports {

    private transient NavController controller;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (container != null) {
            controller = Navigation.findNavController(container);
        }

        ((NavigationActivityInterface) requireActivity()).getFab().setVisibility(View.GONE);
        ((NavigationActivityInterface) requireActivity()).setNavViewVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_reports_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = view.findViewById(R.id.viewpagerReports);
        setupViewPager(viewPager);

        TabLayout tabLayout = view.findViewById(R.id.reportsTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(@NonNull ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());

        Bundle communityBundle = new Bundle();
        communityBundle.putBoolean("isMine", false);

        Bundle myBundle = new Bundle();
        myBundle.putBoolean("isMine", true);

        ReportsListFragment communityList = new ReportsListFragment();
        communityList.setArguments(communityBundle);
        communityList.setManageNavigationReports(this);

        ReportsListFragment myList = new ReportsListFragment();
        myList.setArguments(myBundle);
        myList.setManageNavigationReports(this);

        adapter.addFragment(communityList, getString(R.string.altri_segnalazioni));
        adapter.addFragment(myList, getString(R.string.mie_segnalazioni));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void navigateToReportDetail(Bundle bundle) {
        controller.navigate(R.id.action_reportsDashboardFragment_to_reportDetailFragment, bundle);
    }

    @Override
    public void navigateToAddNewReport() {
        controller.navigate(R.id.action_reportsDashboardFragment_to_reportAddNewFragment);
    }

    @Override
    public void navigateToAddNewReport(Bundle bundle) {
        controller.navigate(R.id.action_reportsDashboardFragment_to_reportAddNewFragment, bundle);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}

