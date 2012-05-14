/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.swingGui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.jme3.input.KeyInput;

/**
 * <code>AWTKeyInput</code>
 *
 * @author Joshua Slack
 * @author Portet to jme3 by user starcom "Paul Kashofer Austria"
 * @version $Revision: 4133 $
 */
public class AWTKeyInput {
    private static final Logger logger = Logger.getLogger(AWTKeyInput.class.getName());

    /**
     * <code>toAWTCode</code> converts KeyInput key codes to AWT key codes.
     *
     * @param key jme KeyInput key code
     * @return awt KeyEvent key code
     */
    public static int toAWTCode( int key ) {
        switch ( key ) {
            case KeyInput.KEY_ESCAPE:
                return KeyEvent.VK_ESCAPE;
            case KeyInput.KEY_1:
                return KeyEvent.VK_1;
            case KeyInput.KEY_2:
                return KeyEvent.VK_2;
            case KeyInput.KEY_3:
                return KeyEvent.VK_3;
            case KeyInput.KEY_4:
                return KeyEvent.VK_4;
            case KeyInput.KEY_5:
                return KeyEvent.VK_5;
            case KeyInput.KEY_6:
                return KeyEvent.VK_6;
            case KeyInput.KEY_7:
                return KeyEvent.VK_7;
            case KeyInput.KEY_8:
                return KeyEvent.VK_8;
            case KeyInput.KEY_9:
                return KeyEvent.VK_9;
            case KeyInput.KEY_0:
                return KeyEvent.VK_0;
            case KeyInput.KEY_MINUS:
                return KeyEvent.VK_MINUS;
            case KeyInput.KEY_EQUALS:
                return KeyEvent.VK_EQUALS;
            case KeyInput.KEY_BACK:
                return KeyEvent.VK_BACK_SPACE;
            case KeyInput.KEY_TAB:
                return KeyEvent.VK_TAB;
            case KeyInput.KEY_Q:
                return KeyEvent.VK_Q;
            case KeyInput.KEY_W:
                return KeyEvent.VK_W;
            case KeyInput.KEY_E:
                return KeyEvent.VK_E;
            case KeyInput.KEY_R:
                return KeyEvent.VK_R;
            case KeyInput.KEY_T:
                return KeyEvent.VK_T;
            case KeyInput.KEY_Y:
                return KeyEvent.VK_Y;
            case KeyInput.KEY_U:
                return KeyEvent.VK_U;
            case KeyInput.KEY_I:
                return KeyEvent.VK_I;
            case KeyInput.KEY_O:
                return KeyEvent.VK_O;
            case KeyInput.KEY_P:
                return KeyEvent.VK_P;
            case KeyInput.KEY_LBRACKET:
                return KeyEvent.VK_OPEN_BRACKET;
            case KeyInput.KEY_RBRACKET:
                return KeyEvent.VK_CLOSE_BRACKET;
            case KeyInput.KEY_RETURN:
                return KeyEvent.VK_ENTER;
            case KeyInput.KEY_LCONTROL:
                return KeyEvent.VK_CONTROL;
            case KeyInput.KEY_A:
                return KeyEvent.VK_A;
            case KeyInput.KEY_S:
                return KeyEvent.VK_S;
            case KeyInput.KEY_D:
                return KeyEvent.VK_D;
            case KeyInput.KEY_F:
                return KeyEvent.VK_F;
            case KeyInput.KEY_G:
                return KeyEvent.VK_G;
            case KeyInput.KEY_H:
                return KeyEvent.VK_H;
            case KeyInput.KEY_J:
                return KeyEvent.VK_J;
            case KeyInput.KEY_K:
                return KeyEvent.VK_K;
            case KeyInput.KEY_L:
                return KeyEvent.VK_L;
            case KeyInput.KEY_SEMICOLON:
                return KeyEvent.VK_SEMICOLON;
            case KeyInput.KEY_APOSTROPHE:
                return KeyEvent.VK_QUOTE;
            case KeyInput.KEY_GRAVE:
                return KeyEvent.VK_DEAD_GRAVE;
            case KeyInput.KEY_LSHIFT:
                return KeyEvent.VK_SHIFT;
            case KeyInput.KEY_BACKSLASH:
                return KeyEvent.VK_BACK_SLASH;
            case KeyInput.KEY_Z:
                return KeyEvent.VK_Z;
            case KeyInput.KEY_X:
                return KeyEvent.VK_X;
            case KeyInput.KEY_C:
                return KeyEvent.VK_C;
            case KeyInput.KEY_V:
                return KeyEvent.VK_V;
            case KeyInput.KEY_B:
                return KeyEvent.VK_B;
            case KeyInput.KEY_N:
                return KeyEvent.VK_N;
            case KeyInput.KEY_M:
                return KeyEvent.VK_M;
            case KeyInput.KEY_COMMA:
                return KeyEvent.VK_COMMA;
            case KeyInput.KEY_PERIOD:
                return KeyEvent.VK_PERIOD;
            case KeyInput.KEY_SLASH:
                return KeyEvent.VK_SLASH;
            case KeyInput.KEY_RSHIFT:
                return KeyEvent.VK_SHIFT;
            case KeyInput.KEY_MULTIPLY:
                return KeyEvent.VK_MULTIPLY;
            case KeyInput.KEY_SPACE:
                return KeyEvent.VK_SPACE;
            case KeyInput.KEY_CAPITAL:
                return KeyEvent.VK_CAPS_LOCK;
            case KeyInput.KEY_F1:
                return KeyEvent.VK_F1;
            case KeyInput.KEY_F2:
                return KeyEvent.VK_F2;
            case KeyInput.KEY_F3:
                return KeyEvent.VK_F3;
            case KeyInput.KEY_F4:
                return KeyEvent.VK_F4;
            case KeyInput.KEY_F5:
                return KeyEvent.VK_F5;
            case KeyInput.KEY_F6:
                return KeyEvent.VK_F6;
            case KeyInput.KEY_F7:
                return KeyEvent.VK_F7;
            case KeyInput.KEY_F8:
                return KeyEvent.VK_F8;
            case KeyInput.KEY_F9:
                return KeyEvent.VK_F9;
            case KeyInput.KEY_F10:
                return KeyEvent.VK_F10;
            case KeyInput.KEY_NUMLOCK:
                return KeyEvent.VK_NUM_LOCK;
            case KeyInput.KEY_SCROLL:
                return KeyEvent.VK_SCROLL_LOCK;
            case KeyInput.KEY_NUMPAD7:
                return KeyEvent.VK_NUMPAD7;
            case KeyInput.KEY_NUMPAD8:
                return KeyEvent.VK_NUMPAD8;
            case KeyInput.KEY_NUMPAD9:
                return KeyEvent.VK_NUMPAD9;
            case KeyInput.KEY_SUBTRACT:
                return KeyEvent.VK_SUBTRACT;
            case KeyInput.KEY_NUMPAD4:
                return KeyEvent.VK_NUMPAD4;
            case KeyInput.KEY_NUMPAD5:
                return KeyEvent.VK_NUMPAD5;
            case KeyInput.KEY_NUMPAD6:
                return KeyEvent.VK_NUMPAD6;
            case KeyInput.KEY_ADD:
                return KeyEvent.VK_ADD;
            case KeyInput.KEY_NUMPAD1:
                return KeyEvent.VK_NUMPAD1;
            case KeyInput.KEY_NUMPAD2:
                return KeyEvent.VK_NUMPAD2;
            case KeyInput.KEY_NUMPAD3:
                return KeyEvent.VK_NUMPAD3;
            case KeyInput.KEY_NUMPAD0:
                return KeyEvent.VK_NUMPAD0;
            case KeyInput.KEY_DECIMAL:
                return KeyEvent.VK_DECIMAL;
            case KeyInput.KEY_F11:
                return KeyEvent.VK_F11;
            case KeyInput.KEY_F12:
                return KeyEvent.VK_F12;
            case KeyInput.KEY_F13:
                return KeyEvent.VK_F13;
            case KeyInput.KEY_F14:
                return KeyEvent.VK_F14;
            case KeyInput.KEY_F15:
                return KeyEvent.VK_F15;
            case KeyInput.KEY_KANA:
                return KeyEvent.VK_KANA;
            case KeyInput.KEY_CONVERT:
                return KeyEvent.VK_CONVERT;
            case KeyInput.KEY_NOCONVERT:
                return KeyEvent.VK_NONCONVERT;
            case KeyInput.KEY_NUMPADEQUALS:
                return KeyEvent.VK_EQUALS;
            case KeyInput.KEY_CIRCUMFLEX:
                return KeyEvent.VK_CIRCUMFLEX;
            case KeyInput.KEY_AT:
                return KeyEvent.VK_AT;
            case KeyInput.KEY_COLON:
                return KeyEvent.VK_COLON;
            case KeyInput.KEY_UNDERLINE:
                return KeyEvent.VK_UNDERSCORE;
            case KeyInput.KEY_STOP:
                return KeyEvent.VK_STOP;
            case KeyInput.KEY_NUMPADENTER:
                return KeyEvent.VK_ENTER;
            case KeyInput.KEY_RCONTROL:
                return KeyEvent.VK_CONTROL;
            case KeyInput.KEY_NUMPADCOMMA:
                return KeyEvent.VK_COMMA;
            case KeyInput.KEY_DIVIDE:
                return KeyEvent.VK_DIVIDE;
            case KeyInput.KEY_PAUSE:
                return KeyEvent.VK_PAUSE;
            case KeyInput.KEY_HOME:
                return KeyEvent.VK_HOME;
            case KeyInput.KEY_UP:
                return KeyEvent.VK_UP;
            case KeyInput.KEY_PRIOR:
                return KeyEvent.VK_PAGE_UP;
            case KeyInput.KEY_LEFT:
                return KeyEvent.VK_LEFT;
            case KeyInput.KEY_RIGHT:
                return KeyEvent.VK_RIGHT;
            case KeyInput.KEY_END:
                return KeyEvent.VK_END;
            case KeyInput.KEY_DOWN:
                return KeyEvent.VK_DOWN;
            case KeyInput.KEY_NEXT:
                return KeyEvent.VK_PAGE_DOWN;
            case KeyInput.KEY_INSERT:
                return KeyEvent.VK_INSERT;
            case KeyInput.KEY_DELETE:
                return KeyEvent.VK_DELETE;
            case KeyInput.KEY_LMENU:
                return KeyEvent.VK_ALT; //todo: location left
            case KeyInput.KEY_RMENU:
                return KeyEvent.VK_ALT; //todo: location right
        }
        logger.warning("unsupported key:" + key);
        return 0x10000 + key;
    }

}
