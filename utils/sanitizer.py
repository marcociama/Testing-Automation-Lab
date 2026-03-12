import os
import subprocess
import shutil

TEST_DIR = "src/test/java/"
MAIN_DIR = "src/main/java/"
BIN_DIR = "temp_bin"

def sanitize():
    print("Inizio bonifica dei test degli studenti...")
    
    # 1. Creiamo una cartella temporanea per i file compilati
    if os.path.exists(BIN_DIR): shutil.rmtree(BIN_DIR)
    os.makedirs(BIN_DIR)

    try:
        # 2. Compiliamo prima le classi di produzione
        # ci servono come riferimento per compilare i test
        print("Compilazione classi di produzione...")
        main_files = [os.path.join(MAIN_DIR, f) for f in os.listdir(MAIN_DIR) if f.endswith(".java")]
        subprocess.run(["javac", "-d", BIN_DIR] + main_files, check=True)

        # 3. Analizziamo ogni test individualmente
        test_files = [f for f in os.listdir(TEST_DIR) if f.endswith(".java")]
        removed_count = 0

        for f in test_files:
            test_path = os.path.join(TEST_DIR, f)
            
            # proviamo a compilare il test usando le classi main già compilate nel classpath
            res = subprocess.run(
                ["javac", "-cp", BIN_DIR, "-d", BIN_DIR, test_path],
                capture_output=True,
                text=True
            )

            if res.returncode != 0:
                print(f"Eliminando {f} (Syntax Error)")
                os.remove(test_path)
                removed_count += 1
        
        print(f"\nBonifica completata. Rimossi {removed_count} file corrotti.")

    except Exception as e:
        print(f"Errore durante la sanitizzazione: {e}")
    finally:
        if os.path.exists(BIN_DIR): shutil.rmtree(BIN_DIR)

if __name__ == "__main__":
    sanitize()