/*
 * Nextcloud Talk application
 *
 * @author Mario Danic
 * Copyright (C) 2017-2018 Mario Danic <mario@lovelyhq.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.nextcloud.talk.models.database;

import android.os.Parcelable;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.nextcloud.talk.models.json.capabilities.Capabilities;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;
import io.requery.Persistable;

@Entity
public interface User extends Parcelable, Persistable, Serializable {
    static final String TAG = "UserEntity";

    @Key
    @Generated
    long getId();

    String getUserId();

    String getUsername();

    String getBaseUrl();

    String getToken();

    String getDisplayName();

    String getPushConfigurationState();

    String getCapabilities();

    String getClientCertificate();

    String getExternalSignalingServer();

    boolean getCurrent();

    boolean getScheduledForDeletion();

    default boolean hasNotificationsCapability(String capabilityName) {
        if (getCapabilities() != null) {
            try {
                Capabilities capabilities = LoganSquare.parse(getCapabilities(), Capabilities.class);
                if (capabilities.getNotificationsCapability() != null && capabilities.getNotificationsCapability().getFeatures() != null) {
                    return capabilities.getSpreedCapability().getFeatures().contains(capabilityName);
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to get capabilities for the user");
            }
        }
        return false;
    }

    default boolean hasExternalCapability(String capabilityName) {
        if (getCapabilities() != null) {
            try {
                Capabilities capabilities = LoganSquare.parse(getCapabilities(), Capabilities.class);
                if (capabilities.getExternalCapability() != null && capabilities.getExternalCapability().containsKey("v1")) {
                    return capabilities.getExternalCapability().get("v1").contains("capabilityName");
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to get capabilities for the user");
            }
        }
        return false;
    }

    default boolean hasSpreedFeatureCapability(String capabilityName) {
        if (getCapabilities() != null) {
            try {
                Capabilities capabilities = LoganSquare.parse(getCapabilities(), Capabilities.class);
                if (capabilities != null && capabilities.getSpreedCapability() != null &&
                        capabilities.getSpreedCapability().getFeatures() != null) {
                    return capabilities.getSpreedCapability().getFeatures().contains(capabilityName);
                }
            } catch (IOException e) {
                Log.e(TAG, "Failed to get capabilities for the user");
            }
        }
        return false;
    }

    default int getMessageMaxLength() {
        if (getCapabilities() != null) {
            Capabilities capabilities = null;
            try {
                capabilities = LoganSquare.parse(getCapabilities(), Capabilities.class);
                if (capabilities != null && capabilities.getSpreedCapability() != null && capabilities.getSpreedCapability().getConfig() != null
                        && capabilities.getSpreedCapability().getConfig().containsKey("chat")) {
                    HashMap<String, String> chatConfigHashMap = capabilities.getSpreedCapability().getConfig().get("chat");
                    if (chatConfigHashMap != null && chatConfigHashMap.containsKey("max-length")) {
                        int chatSize = Integer.parseInt(chatConfigHashMap.get("max-length"));
                        if (chatSize > 0) {
                            return chatSize;
                        } else {
                            return 1000;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 1000;
    }

    default boolean isPhoneBookIntegrationAvailable() {
        if (getCapabilities() != null) {
            Capabilities capabilities;
            try {
                capabilities = LoganSquare.parse(getCapabilities(), Capabilities.class);
                return capabilities != null &&
                        capabilities.getSpreedCapability() != null &&
                        capabilities.getSpreedCapability().getFeatures() != null &&
                        capabilities.getSpreedCapability().getFeatures().contains("phonebook-search");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    default boolean isReadStatusAvailable() {
        if (getCapabilities() != null) {
            Capabilities capabilities;
            try {
                capabilities = LoganSquare.parse(getCapabilities(), Capabilities.class);
                if (capabilities != null &&
                        capabilities.getSpreedCapability() != null &&
                        capabilities.getSpreedCapability().getConfig() != null &&
                        capabilities.getSpreedCapability().getConfig().containsKey("chat")) {
                    HashMap<String, String> map = capabilities.getSpreedCapability().getConfig().get("chat");
                    return map != null && map.containsKey("read-privacy");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    default boolean isReadStatusPrivate() {
        if (getCapabilities() != null) {
            Capabilities capabilities;
            try {
                capabilities = LoganSquare.parse(getCapabilities(), Capabilities.class);
                if (capabilities != null &&
                        capabilities.getSpreedCapability() != null &&
                        capabilities.getSpreedCapability().getConfig() != null &&
                        capabilities.getSpreedCapability().getConfig().containsKey("chat")) {
                    HashMap<String, String> map = capabilities.getSpreedCapability().getConfig().get("chat");
                    if (map != null && map.containsKey("read-privacy")) {
                        return Integer.parseInt(map.get("read-privacy")) == 1;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    default String getAttachmentFolder() {
        if (getCapabilities() != null) {
            Capabilities capabilities;
            try {
                capabilities = LoganSquare.parse(getCapabilities(), Capabilities.class);
                if (capabilities != null &&
                        capabilities.getSpreedCapability() != null &&
                        capabilities.getSpreedCapability().getConfig() != null &&
                        capabilities.getSpreedCapability().getConfig().containsKey("attachments")) {
                    HashMap<String, String> map = capabilities.getSpreedCapability().getConfig().get("attachments");
                    if (map != null && map.containsKey("folder")) {
                        return map.get("folder");
                    }
                }
            } catch (IOException e) {
                Log.e("User.java", "Failed to get attachment folder", e);
            }
        }
        return "/Talk";
    }

    default String getServerName() {
        if (getCapabilities() != null) {
            Capabilities capabilities;
            try {
                capabilities = LoganSquare.parse(getCapabilities(), Capabilities.class);
                if (capabilities != null && capabilities.getThemingCapability() != null) {
                    return capabilities.getThemingCapability().getName();
                }
            } catch (IOException e) {
                Log.e("User.java", "Failed to get server name", e);
            }
        }
        return "";
    }
}
