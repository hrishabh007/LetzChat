package com.app.letzchat.tasks;

import android.content.ContentValues;
import android.content.Context;


import com.app.letzchat.SmackInvocationException;
import com.app.letzchat.databases.ChatMessageTableHelper;
import com.app.letzchat.xmpp.SmackHelper;
import com.app.letzchat.xmpp.UserLocation;

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * Created by dilli on 11/27/2015.
 */
public class SendLocationTask extends SendMessageTask {
    private UserLocation location;
    protected PacketExtension packetExtension;

    public SendLocationTask(Response.Listener<Boolean> listener, Context context, String to, String nickname, UserLocation location) {
        super(listener, context, to, nickname, location.getName());

        this.location = location;
        packetExtension = location;
    }

    @Override
    protected ContentValues newMessage(long timeMillis) {
        return ChatMessageTableHelper.newLocationMessage(to, body, timeMillis, location, true);
    }

    @Override
    protected void doSend(Context context) throws SmackInvocationException {
        SmackHelper.getInstance(context).sendChatMessage(to, body, packetExtension);
    }
}