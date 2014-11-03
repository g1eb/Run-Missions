package nl.gleb.runmissions;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ValueEventListener;

/**
 * Created by Gleb on 03/11/14.
 */
public class Chat extends Fragment {

    private static final String USERNAME = "username";

    Comm comm;
    EditText input;
    String username;
    private ValueEventListener connectedListener;
    private ChatListAdapter chatListAdapter;

    public static Chat newInstance(String username) {
        Chat fragment = new Chat();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        comm = (Comm) activity;
        comm.setTitle(getString(R.string.title_chat));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Setup our input methods. Enter key on the keyboard or pushing the send button

        input = (EditText) getActivity().findViewById(R.id.messageInput);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        getActivity().findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        final ListView listView = (ListView) getActivity().findViewById(R.id.chat_list);

        // Tell our list adapter that we only want 50 messages at a time
        chatListAdapter = new ChatListAdapter(Main.chatRef.limit(50), getActivity(), R.layout.chat_message, getArguments().getString(USERNAME));
        listView.setAdapter(chatListAdapter);
        chatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatListAdapter.getCount() - 1);
            }
        });
    }

    private void sendMessage() {
        String message = input.getText().toString().trim();
        if (!message.equals("")) {
            comm.sendMessage(message);
            input.setText("");
        }
    }
}
