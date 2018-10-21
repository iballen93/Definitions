package com.components.actions;

class HtmlFormatter {
    static String applyLink(String link, String text) {
        return "<a href=\"" + link + "\">" + text + "</a>";
    }

    static String applyBold(String input) {
        return "<b>" + input + "</b>";
    }

    static String applyFontColor(String color, String input) {
        return "<font color=\"" + color + "\">" + input + "</font>";
    }

    static String applyFontFace(String face, String input) {
        return "<font face=\"" + face + "\">" + input + "</font>";
    }

    static String applyFontSize(int size, String input) {
        return "<font size=\"" + size + "\">" + input + "</font>";
    }
}