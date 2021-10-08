package ci;

class Token {
  final Object    literal;
  final String    lexeme;
  final TokenType type;
  final int       line;

  Token(TokenType type, String lexeme, Object literal, int line) {
    this.type    = type;
    this.lexeme  = lexeme;
    this.literal = literal;
    this.line    = line;
  }

  public String toString() {
    return type + " " + lexeme + " " + literal;
  }
}
