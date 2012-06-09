/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package com.crashnote.external.config.impl;

import java.util.List;

import com.crashnote.external.config.ConfigException;
import com.crashnote.external.config.ConfigOrigin;
import com.crashnote.external.config.ConfigValueType;

/* FIXME the way the subclasses of Token are private with static isFoo and accessors is kind of ridiculous. */
final class Tokens {
    static private class Value extends Token {

        final private AbstractConfigValue value;

        Value(final AbstractConfigValue value) {
            super(TokenType.VALUE, value.origin());
            this.value = value;
        }

        AbstractConfigValue value() {
            return value;
        }

        @Override
        public String toString() {
            return "'" + value().unwrapped() + "' (" + value.valueType().name() + ")";
        }

        @Override
        protected boolean canEqual(final Object other) {
            return other instanceof Value;
        }

        @Override
        public boolean equals(final Object other) {
            return super.equals(other) && ((Value) other).value.equals(value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + value.hashCode();
        }
    }

    static private class Line extends Token {
        Line(final ConfigOrigin origin) {
            super(TokenType.NEWLINE, origin);
        }

        @Override
        public String toString() {
            return "'\\n'@" + lineNumber();
        }

        @Override
        protected boolean canEqual(final Object other) {
            return other instanceof Line;
        }

        @Override
        public boolean equals(final Object other) {
            return super.equals(other) && ((Line) other).lineNumber() == lineNumber();
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + lineNumber();
        }
    }

    // This is not a Value, because it requires special processing
    static private class UnquotedText extends Token {
        final private String value;

        UnquotedText(final ConfigOrigin origin, final String s) {
            super(TokenType.UNQUOTED_TEXT, origin);
            this.value = s;
        }

        String value() {
            return value;
        }

        @Override
        public String toString() {
            return "'" + value + "'";
        }

        @Override
        protected boolean canEqual(final Object other) {
            return other instanceof UnquotedText;
        }

        @Override
        public boolean equals(final Object other) {
            return super.equals(other)
                    && ((UnquotedText) other).value.equals(value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + value.hashCode();
        }
    }

    static private class Problem extends Token {
        final private String what;
        final private String message;
        final private boolean suggestQuotes;
        final private Throwable cause;

        Problem(final ConfigOrigin origin, final String what, final String message, final boolean suggestQuotes,
                final Throwable cause) {
            super(TokenType.PROBLEM, origin);
            this.what = what;
            this.message = message;
            this.suggestQuotes = suggestQuotes;
            this.cause = cause;
        }

        String message() {
            return message;
        }

        boolean suggestQuotes() {
            return suggestQuotes;
        }

        Throwable cause() {
            return cause;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append('\'');
            sb.append(what);
            sb.append('\'');
            return sb.toString();
        }

        @Override
        protected boolean canEqual(final Object other) {
            return other instanceof Problem;
        }

        @Override
        public boolean equals(final Object other) {
            return super.equals(other) && ((Problem) other).what.equals(what)
                    && ((Problem) other).message.equals(message)
                    && ((Problem) other).suggestQuotes == suggestQuotes
                    && ConfigImplUtil.equalsHandlingNull(((Problem) other).cause, cause);
        }

        @Override
        public int hashCode() {
            int h = 41 * (41 + super.hashCode());
            h = 41 * (h + what.hashCode());
            h = 41 * (h + message.hashCode());
            h = 41 * (h + Boolean.valueOf(suggestQuotes).hashCode());
            if (cause != null)
                h = 41 * (h + cause.hashCode());
            return h;
        }
    }

    static private class Comment extends Token {
        final private String text;

        Comment(final ConfigOrigin origin, final String text) {
            super(TokenType.COMMENT, origin);
            this.text = text;
        }

        String text() {
            return text;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("'#");
            sb.append(text);
            sb.append("' (COMMENT)");
            return sb.toString();
        }

        @Override
        protected boolean canEqual(final Object other) {
            return other instanceof Comment;
        }

        @Override
        public boolean equals(final Object other) {
            return super.equals(other) && ((Comment) other).text.equals(text);
        }

        @Override
        public int hashCode() {
            int h = 41 * (41 + super.hashCode());
            h = 41 * (h + text.hashCode());
            return h;
        }
    }

    // This is not a Value, because it requires special processing
    static private class Substitution extends Token {
        final private boolean optional;
        final private List<Token> value;

        Substitution(final ConfigOrigin origin, final boolean optional, final List<Token> expression) {
            super(TokenType.SUBSTITUTION, origin);
            this.optional = optional;
            this.value = expression;
        }

        boolean optional() {
            return optional;
        }

        List<Token> value() {
            return value;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            for (final Token t : value) {
                sb.append(t.toString());
            }
            return "'${" + sb.toString() + "}'";
        }

        @Override
        protected boolean canEqual(final Object other) {
            return other instanceof Substitution;
        }

        @Override
        public boolean equals(final Object other) {
            return super.equals(other)
                    && ((Substitution) other).value.equals(value);
        }

        @Override
        public int hashCode() {
            return 41 * (41 + super.hashCode()) + value.hashCode();
        }
    }

    static boolean isValue(final Token token) {
        return token instanceof Value;
    }

    static AbstractConfigValue getValue(final Token token) {
        if (token instanceof Value) {
            return ((Value) token).value();
        } else {
            throw new ConfigException.BugOrBroken(
                    "tried to get value of non-value token " + token);
        }
    }

    static boolean isValueWithType(final Token t, final ConfigValueType valueType) {
        return isValue(t) && getValue(t).valueType() == valueType;
    }

    static boolean isNewline(final Token token) {
        return token instanceof Line;
    }

    static boolean isProblem(final Token token) {
        return token instanceof Problem;
    }

    static String getProblemMessage(final Token token) {
        if (token instanceof Problem) {
            return ((Problem) token).message();
        } else {
            throw new ConfigException.BugOrBroken("tried to get problem message from " + token);
        }
    }

    static boolean getProblemSuggestQuotes(final Token token) {
        if (token instanceof Problem) {
            return ((Problem) token).suggestQuotes();
        } else {
            throw new ConfigException.BugOrBroken("tried to get problem suggestQuotes from "
                    + token);
        }
    }

    static Throwable getProblemCause(final Token token) {
        if (token instanceof Problem) {
            return ((Problem) token).cause();
        } else {
            throw new ConfigException.BugOrBroken("tried to get problem cause from " + token);
        }
    }

    static boolean isComment(final Token token) {
        return token instanceof Comment;
    }

    static String getCommentText(final Token token) {
        if (token instanceof Comment) {
            return ((Comment) token).text();
        } else {
            throw new ConfigException.BugOrBroken("tried to get comment text from " + token);
        }
    }

    static boolean isUnquotedText(final Token token) {
        return token instanceof UnquotedText;
    }

    static String getUnquotedText(final Token token) {
        if (token instanceof UnquotedText) {
            return ((UnquotedText) token).value();
        } else {
            throw new ConfigException.BugOrBroken(
                    "tried to get unquoted text from " + token);
        }
    }

    static boolean isSubstitution(final Token token) {
        return token instanceof Substitution;
    }

    static List<Token> getSubstitutionPathExpression(final Token token) {
        if (token instanceof Substitution) {
            return ((Substitution) token).value();
        } else {
            throw new ConfigException.BugOrBroken(
                    "tried to get substitution from " + token);
        }
    }

    static boolean getSubstitutionOptional(final Token token) {
        if (token instanceof Substitution) {
            return ((Substitution) token).optional();
        } else {
            throw new ConfigException.BugOrBroken("tried to get substitution optionality from "
                    + token);
        }
    }

    final static Token START = Token.newWithoutOrigin(TokenType.START, "start of file");
    final static Token END = Token.newWithoutOrigin(TokenType.END, "end of file");
    final static Token COMMA = Token.newWithoutOrigin(TokenType.COMMA, "','");
    final static Token EQUALS = Token.newWithoutOrigin(TokenType.EQUALS, "'='");
    final static Token COLON = Token.newWithoutOrigin(TokenType.COLON, "':'");
    final static Token OPEN_CURLY = Token.newWithoutOrigin(TokenType.OPEN_CURLY, "'{'");
    final static Token CLOSE_CURLY = Token.newWithoutOrigin(TokenType.CLOSE_CURLY, "'}'");
    final static Token OPEN_SQUARE = Token.newWithoutOrigin(TokenType.OPEN_SQUARE, "'['");
    final static Token CLOSE_SQUARE = Token.newWithoutOrigin(TokenType.CLOSE_SQUARE, "']'");
    final static Token PLUS_EQUALS = Token.newWithoutOrigin(TokenType.PLUS_EQUALS, "'+='");

    static Token newLine(final ConfigOrigin origin) {
        return new Line(origin);
    }

    static Token newProblem(final ConfigOrigin origin, final String what, final String message,
            final boolean suggestQuotes, final Throwable cause) {
        return new Problem(origin, what, message, suggestQuotes, cause);
    }

    static Token newComment(final ConfigOrigin origin, final String text) {
        return new Comment(origin, text);
    }

    static Token newUnquotedText(final ConfigOrigin origin, final String s) {
        return new UnquotedText(origin, s);
    }

    static Token newSubstitution(final ConfigOrigin origin, final boolean optional, final List<Token> expression) {
        return new Substitution(origin, optional, expression);
    }

    static Token newValue(final AbstractConfigValue value) {
        return new Value(value);
    }

    static Token newString(final ConfigOrigin origin, final String value) {
        return newValue(new ConfigString(origin, value));
    }

    static Token newInt(final ConfigOrigin origin, final int value, final String originalText) {
        return newValue(ConfigNumber.newNumber(origin, value,
                originalText));
    }

    static Token newDouble(final ConfigOrigin origin, final double value,
            final String originalText) {
        return newValue(ConfigNumber.newNumber(origin, value,
                originalText));
    }

    static Token newLong(final ConfigOrigin origin, final long value, final String originalText) {
        return newValue(ConfigNumber.newNumber(origin, value,
                originalText));
    }

    static Token newNull(final ConfigOrigin origin) {
        return newValue(new ConfigNull(origin));
    }

    static Token newBoolean(final ConfigOrigin origin, final boolean value) {
        return newValue(new ConfigBoolean(origin, value));
    }
}
