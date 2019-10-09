/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.core.thing.events;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.CommonTriggerEvents;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.builder.ThingBuilder;
import org.eclipse.smarthome.core.thing.binding.builder.ThingStatusInfoBuilder;
import org.eclipse.smarthome.core.thing.dto.ThingDTOMapper;
import org.eclipse.smarthome.core.thing.events.ThingEventFactory.TriggerEventPayloadBean;
import org.eclipse.smarthome.test.java.JavaOSGiTest;
import org.junit.Test;

import com.google.gson.Gson;

/**
 * {@link ThingEventFactoryTests} tests the {@link ThingEventFactory}.
 *
 * @author Stefan Bußweiler - Initial contribution
 */
public class ThingEventFactoryTest extends JavaOSGiTest {
    private static final ThingStatusInfo THING_STATUS_INFO = ThingStatusInfoBuilder
            .create(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR).withDescription("Some description")
            .build();

    private final ThingEventFactory factory = new ThingEventFactory();

    private static final ThingTypeUID THING_TYPE_UID = new ThingTypeUID("binding:type");
    private static final ThingUID THING_UID = new ThingUID(THING_TYPE_UID, "id");
    private static final Thing THING = ThingBuilder.create(THING_TYPE_UID, THING_UID).build();

    private static final String THING_STATUS_EVENT_TOPIC = ThingEventFactory.THING_STATUS_INFO_EVENT_TOPIC
            .replace("{thingUID}", THING_UID.getAsString());
    private static final String THING_ADDED_EVENT_TOPIC = ThingEventFactory.THING_ADDED_EVENT_TOPIC
            .replace("{thingUID}", THING_UID.getAsString());

    private static final String THING_STATUS_EVENT_PAYLOAD = new Gson().toJson(THING_STATUS_INFO);
    private static final String THING_ADDED_EVENT_PAYLOAD = new Gson().toJson(ThingDTOMapper.map(THING));

    private static final ChannelUID CHANNEL_UID = new ChannelUID(THING_UID, "channel");
    private static final String CHANNEL_TRIGGERED_EVENT_TOPIC = ThingEventFactory.CHANNEL_TRIGGERED_EVENT_TOPIC
            .replace("{channelUID}", CHANNEL_UID.getAsString());
    private static final String CHANNEL_TRIGGERED_EVENT_PAYLOAD = new Gson()
            .toJson(new TriggerEventPayloadBean(CommonTriggerEvents.PRESSED, CHANNEL_UID.getAsString()));

    @Test
    public void testCreateEvent_ThingStatusInfoEvent() throws Exception {
        Event event = factory.createEvent(ThingStatusInfoEvent.TYPE, THING_STATUS_EVENT_TOPIC,
                THING_STATUS_EVENT_PAYLOAD, null);

        assertThat(event, is(instanceOf(ThingStatusInfoEvent.class)));
        ThingStatusInfoEvent statusEvent = (ThingStatusInfoEvent) event;
        assertEquals(ThingStatusInfoEvent.TYPE, statusEvent.getType());
        assertEquals(THING_STATUS_EVENT_TOPIC, statusEvent.getTopic());
        assertEquals(THING_STATUS_EVENT_PAYLOAD, statusEvent.getPayload());
        assertEquals(THING_STATUS_INFO, statusEvent.getStatusInfo());
        assertEquals(THING_UID, statusEvent.getThingUID());
    }

    @Test
    public void testCreateStatusInfoEvent() {
        ThingStatusInfoEvent event = ThingEventFactory.createStatusInfoEvent(THING_UID, THING_STATUS_INFO);

        assertEquals(ThingStatusInfoEvent.TYPE, event.getType());
        assertEquals(THING_STATUS_EVENT_TOPIC, event.getTopic());
        assertEquals(THING_STATUS_EVENT_PAYLOAD, event.getPayload());
        assertEquals(THING_STATUS_INFO, event.getStatusInfo());
        assertEquals(THING_UID, event.getThingUID());
    }

    @Test
    public void testCreateEvent_ThingAddedEvent() throws Exception {
        Event event = factory.createEvent(ThingAddedEvent.TYPE, THING_ADDED_EVENT_TOPIC, THING_ADDED_EVENT_PAYLOAD,
                null);

        assertThat(event, is(instanceOf(ThingAddedEvent.class)));
        ThingAddedEvent addedEvent = (ThingAddedEvent) event;
        assertEquals(ThingAddedEvent.TYPE, addedEvent.getType());
        assertEquals(THING_ADDED_EVENT_TOPIC, addedEvent.getTopic());
        assertEquals(THING_ADDED_EVENT_PAYLOAD, addedEvent.getPayload());
        assertNotNull(addedEvent.getThing());
        assertEquals(THING_UID.getAsString(), addedEvent.getThing().UID);
    }

    @Test
    public void testCreateAddedEvent() {
        ThingAddedEvent event = ThingEventFactory.createAddedEvent(THING);

        assertEquals(ThingAddedEvent.TYPE, event.getType());
        assertEquals(THING_ADDED_EVENT_TOPIC, event.getTopic());
        assertEquals(THING_ADDED_EVENT_PAYLOAD, event.getPayload());
        assertNotNull(event.getThing());
        assertEquals(THING_UID.getAsString(), event.getThing().UID);
    }

    @Test
    public void testCreateTriggerEvent() {
        ChannelTriggeredEvent event = ThingEventFactory.createTriggerEvent(CommonTriggerEvents.PRESSED, CHANNEL_UID);

        assertEquals(ChannelTriggeredEvent.TYPE, event.getType());
        assertEquals(CHANNEL_TRIGGERED_EVENT_TOPIC, event.getTopic());
        assertEquals(CHANNEL_TRIGGERED_EVENT_PAYLOAD, event.getPayload());
        assertNotNull(event.getEvent());
        assertEquals(CommonTriggerEvents.PRESSED, event.getEvent());
    }

    @Test
    public void testCreateEvent_ChannelTriggeredEvent() throws Exception {
        Event event = factory.createEvent(ChannelTriggeredEvent.TYPE, CHANNEL_TRIGGERED_EVENT_TOPIC,
                CHANNEL_TRIGGERED_EVENT_PAYLOAD, null);

        assertThat(event, is(instanceOf(ChannelTriggeredEvent.class)));
        ChannelTriggeredEvent triggeredEvent = (ChannelTriggeredEvent) event;
        assertEquals(ChannelTriggeredEvent.TYPE, triggeredEvent.getType());
        assertEquals(CHANNEL_TRIGGERED_EVENT_TOPIC, triggeredEvent.getTopic());
        assertEquals(CHANNEL_TRIGGERED_EVENT_PAYLOAD, triggeredEvent.getPayload());
        assertNotNull(triggeredEvent.getEvent());
        assertEquals(CommonTriggerEvents.PRESSED, triggeredEvent.getEvent());
    }
}
