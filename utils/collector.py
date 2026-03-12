import os
import shutil
import re

SOURCE_ROOT = ""
TARGET_DIR = ""
CLASSES = ["HSLColor", "SillabatoreItaliano", "SubjectParser", "TennisScoreManager"]

def smart_collect():
    # Struttura: { (Player, Class): { 'game': id, 'round': id, 'turn': id, 'path': full_path } }
    best_tests = {}

    for root, _, files in os.walk(SOURCE_ROOT):
        for file in files:
            if file.startswith("TestTest") and file.endswith(".java"):
                # Estraggo gli ID con Regex (es. Player105, Game4, Round1, Turn1)
                p_id = int(re.search(r'Player(\d+)', root).group(1))
                g_id = int(re.search(r'Game(\d+)', root).group(1))
                r_id = int(re.search(r'Round(\d+)', root).group(1))
                t_id = int(re.search(r'Turn(\d+)', root).group(1))
                
                # Identifico la classe dal nome file
                target_cls = next((c for c in CLASSES if c in file), None)
                if not target_cls: continue

                key = (p_id, target_cls)
                current_data = {'g': g_id, 'r': r_id, 't': t_id, 'path': os.path.join(root, file)}

                # Logica di selezione: vince il Game ID più alto, poi Round, poi Turn
                if key not in best_tests:
                    best_tests[key] = current_data
                else:
                    prev = best_tests[key]
                    if (g_id, r_id, t_id) > (prev['g'], prev['r'], prev['t']):
                        best_tests[key] = current_data

    # Copia dei file selezionati
    if os.path.exists(TARGET_DIR): shutil.rmtree(TARGET_DIR)
    os.makedirs(TARGET_DIR)

    for i, ((p_id, cls), data) in enumerate(best_tests.items(), 1):
        new_name = f"TestTest{cls}_P{p_id}_G{data['g']}R{data['r']}.java"
        shutil.copy2(data['path'], os.path.join(TARGET_DIR, new_name))

    print(f"Estratti {len(best_tests)} test finali.") # i 'migliori' per ogni studente

if __name__ == "__main__":
    smart_collect()