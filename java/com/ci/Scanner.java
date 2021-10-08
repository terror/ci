package ci;

import static ci.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and",    AND);
    keywords.put("class",  CLASS);
    keywords.put("else",   ELSE);
    keywords.put("false",  FALSE);
    keywords.put("for",    FOR);
    keywords.put("fun",    FUN);
    keywords.put("if",     IF);
    keywords.put("nil",    NIL);
    keywords.put("or",     OR);
    keywords.put("print",  PRINT);
    keywords.put("return", RETURN);
    keywords.put("super",  SUPER);
    keywords.put("this",   THIS);
    keywords.put("true",   TRUE);
    keywords.put("var",    VAR);
    keywords.put("while",  WHILE);
  }

  private int start = 0;
  private int curr = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  private boolean isAtEnd() {
    return curr >= source.length();
  }

  private char advance() {
    if (isAtEnd()) return '\0';
    return source.charAt(curr++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, curr);
    tokens.add(new Token(type, text, literal, line));
  }

  private boolean match(char expected) {
    if (isAtEnd()) return false;
    if (source.charAt(curr) != expected) return false;
    ++curr;
    return true;
  }

  private char peek() {
    if (isAtEnd()) return '\0';
    return source.charAt(curr);
  }

  private char peekNext() {
    if (curr + 1 >= source.length()) return '\0';
    return source.charAt(curr + 1);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private boolean isAlphanumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private void scanToken() {
    char c = advance();
    switch(c) {
      // single character lexemes
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;

      // operators
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;

      // special handling for '/' because of comments
      case '/':
        if (match('/'))
          while (peek() != '\n' && !isAtEnd()) advance();
        else
          addToken(SLASH);
        break;

      // whitespace
      case ' ':
      case '\r':
      case '\t':
        break;

      // newline
      case '\n':
        ++line;
        break;

      // literals
      case '"':
        string();
        break;

      default:
        if (isDigit(c))
          number();
        else if (isAlpha(c))
          identifier();
        else
          Lox.error(line, "Unexpected character.");
        break;
    }
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') ++line;
      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    advance();
    addToken(STRING, source.substring(start + 1, curr - 1));
  }

  private void number() {
    while (isDigit(peek())) advance();

    if (peek() == '.' && isDigit(peekNext())); {
      advance();
      while (isDigit(peek())) advance();
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, curr)));
  }

  private void identifier() {
    while (isAlphanumeric(peek())) advance();
    String text = source.substring(start, curr);
    TokenType type = keywords.get(text);
    if (type == null) type = IDENTIFIER;
    addToken(type);
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = curr;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }
}
