set dotenv-load

export EDITOR := 'nvim'

# switch these out based on whatever
# language is being used
alias c := compile-java
alias r := run-java

pkg := 'ci'

default:
  just --list

compile-java:
	javac	./java/com/{{ pkg }}/*.java -d .

run-java:
	java {{ pkg }}.Main

compile-c:
	gcc ./c/main.c

run-c:
	./a.out
