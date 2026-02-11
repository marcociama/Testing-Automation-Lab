import os
import re

def get_java_classes(directory):
    """
    Funzione usata per scannerizzare tutte le classi nella directory src/main/java
    a partire da quelle si cercano le parole chiave nelle classi di test
    """
    classes = {} # dizionario vuoto
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".java"):
                name = os.path.splitext(file)[0] # per ogni file prendo il nome prima del suffisso .java
                classes[name] = os.path.join(root, file) # si compone la directory es root = "src/main/java" + file = "Calcolatrice.java"
    return classes

def count_occurrences(file_path, class_names):
    with open(file_path, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read() # leggo il codice

        # rimozione commenti per pulizia
        content = re.sub(r'//.*|/\*.*?\*/', '', content, flags=re.DOTALL)

        counts = {} # dizionario per contare le occorrenze per ogni nome di classe
        for name in class_names:
            # Cerca la classe come parola intera (\b)
            matches = re.findall(rf'\b{name}\b', content) # ritorna la lista dei match: ogni match è esattamente la parola f{name} non
            counts[name] = len(matches) # determino il numero di occorrenze
    return counts

def main():
    main_dir = "src/main/java"
    test_dir = "src/test/java"
    project_name = "Testing-Automation-Lab"

    uut_classes = get_java_classes(main_dir)
    test_files = [] # trovo files di test
    for root, _, files in os.walk(test_dir):
        for file in files:
            if file.endswith(".java"):
                test_files.append(os.path.join(root, file))

    with open("input.csv", "w", encoding='utf-8') as f: # creo csv per tsDetect
        for test_path in test_files:
            counts = count_occurrences(test_path, uut_classes.keys())

            # trovo la classe con più occorrenze, se presente
            best_match = max(counts, key=counts.get) if any(counts.values()) else None

            if best_match and counts[best_match] > 0:
                uut_path = uut_classes[best_match]
                f.write(f"{project_name},{test_path},{uut_path}\n") # scrivo il path nel csv
            else:
                # se non trovo nulla, associo il test a se stesso per non bloccare tsDetect
                f.write(f"{project_name},{test_path},{test_path}\n")

if __name__ == "__main__":
    main()