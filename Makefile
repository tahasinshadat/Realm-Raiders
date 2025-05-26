# === CONFIG ===
SRC_DIR := src
BIN_DIR := bin
LIB_DIR := lib
MAIN_CLASS := main.Main

# === Detect OS and Set Classpath Separator ===
ifeq ($(OS),Windows_NT)
  SEP := ;
else
  SEP := :
endif

# === Libraries and Java Files ===
LIB_JARS := $(wildcard $(LIB_DIR)/*.jar)
CLASSPATH := $(subst $(space),$(SEP),$(strip $(LIB_JARS)))$(SEP)$(BIN_DIR) # needs to be fixed
JAVA_FILES := $(shell find $(SRC_DIR) -name "*.java")

# === Targets ===
.PHONY: all run clean

all:
	mkdir -p $(BIN_DIR)
	javac -cp "$(CLASSPATH)" -d $(BIN_DIR) $(JAVA_FILES)

run: all
	java -cp "$(CLASSPATH)" $(MAIN_CLASS)

clean:
	rm -rf $(BIN_DIR)
