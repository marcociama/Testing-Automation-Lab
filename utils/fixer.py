import os
import re

TEST_DIR = ""
MAIN_DIR = ""

def ultra_clean(directory):
    print(f"--- Operando su: {directory} ---")
    for filename in os.listdir(directory):
        if filename.endswith(".java"):
            path = os.path.join(directory, filename)
            expected_class_name = filename.replace(".java", "")
            
            with open(path, 'r', encoding='utf-8', errors='ignore') as f:
                content = f.read()

            # 1. RIMOZIONE EVENTUALI PACKAGE
            content = re.sub(r'package\s+.*?;', '', content, flags=re.DOTALL)

            # 2. DE-PUBLIC
            # Questa regex trova 'public' seguito da qualsiasi spazio/invio e poi 'class'
            content = re.sub(r'public\s+class', 'class', content, flags=re.MULTILINE)

            # 3. SINCRONIZZAZIONE NOME CLASSE
            # Trova 'class' seguito da qualsiasi spazio e poi il nome vecchio, e lo cambia.
            content = re.sub(r'(class\s+)[A-Za-z0-9_]+', r'\1' + expected_class_name, content, count=1)

            with open(path, 'w', encoding='utf-8') as f:
                f.write(content)
    print(f"Fatto.")

if __name__ == "__main__":
    ultra_clean(MAIN_DIR)
    ultra_clean(TEST_DIR)