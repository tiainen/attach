/*
 * Copyright (c) 2016, 2019, Gluon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.attach.pushnotifications.impl;

import com.gluonhq.attach.pushnotifications.PushNotificationsService;
import com.gluonhq.attach.runtimeargs.RuntimeArgsService;
import com.gluonhq.attach.util.Constants;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 * iOS implementation of PushNotificationsService.
 */
public class IOSPushNotificationsService implements PushNotificationsService {

    static {
        System.loadLibrary("PushNotifications");
    }

    /**
     * A string property to wrap the token device when received from the native layer
     */
    private static final ReadOnlyStringWrapper TOKEN = new ReadOnlyStringWrapper();

    public IOSPushNotificationsService() {
        if ("true".equals(System.getProperty(Constants.ATTACH_DEBUG))) {
            enableDebug();
        }
        
        // Initialize RAS service
        RuntimeArgsService.create();
    }

    @Override
    public ReadOnlyStringProperty tokenProperty() {
        return TOKEN.getReadOnlyProperty();
    }

    @Override
    public void register() {
        initPushNotifications();
    }

    // native
    private static native void initPushNotifications();
    private static native void enableDebug();
    
    /**
     * @param s String with the error description
     */
    private static void failToRegisterForRemoteNotifications(String s) {
        Platform.runLater(() -> System.out.println("Failed registering Push Notifications with error: " + s));
    }

    /**
     * @param token String with the device token description
     */
    private static void didRegisterForRemoteNotifications(String token) {
        if (token == null) {
            return;
        }
        Platform.runLater(() -> TOKEN.setValue(token));
    }
}