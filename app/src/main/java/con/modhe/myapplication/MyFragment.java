package con.modhe.myapplication;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class MyFragment extends Fragment {
    private Activity activity;

    public Context getContext() {
        if (activity == null) {
            return App.getContext();
        }
        return activity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = getActivity();
    }
}
