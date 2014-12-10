package org.hcsoups.hardcore.utils;

import org.apache.commons.lang.Validate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


/**
 * This class wraps the {@link net.minecraft.server.v1_*_R*.PacketPlayOutChat} class using reflection.
 * With this we can simply instantiate this class and run the {@link WrappedServerPlayChatPacket#getPacket()} method
 * and then send the object too the client as a packet either with {@link ReflectionUtils#sendPacket(org.bukkit.entity.Player, Object)} or
 * through other means.
 * 
 * 
 * @author Ryan (rbrick)
 * @since 10-25-2014
 */
public class WrappedServerPlayChatPacket {

    Object packet;

    Class<?> TYPE = ReflectionUtils.getCraftClass("PacketPlayOutChat");

    Class<?> CHAT_SERIALIZER = ReflectionUtils.getCraftClass("ChatSerializer");

    Class<?> ICHAT_BASE_COMPONENT = ReflectionUtils.getCraftClass("IChatBaseComponent");

    public WrappedServerPlayChatPacket(String json) {
        Validate.notNull(json);
        Validate.notEmpty(json);
        try {
            Constructor<?> packet_construct = TYPE.getConstructor(ICHAT_BASE_COMPONENT);

            // This method is static so invoke 'null' for the object argument.
            Method m = CHAT_SERIALIZER.getMethod("a", String.class);
            Object chat_base_component_object = m.invoke(null, json);
            packet = packet_construct.newInstance(chat_base_component_object);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public Object getPacket() {
        return packet;
    }
	
}
