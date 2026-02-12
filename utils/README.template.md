# Testing-Automation-Lab 🚀

## 📊 Stato del Progetto
* **Code Coverage:** {{COVERAGE}}
* **Test Smells rilevati:** {{SMELLS}}
* **Ultima Build:** ![Build Status](https://github.com/marcociama/Testing-Automation-Lab/actions/workflows/pipeline.yml/badge.svg)

## 🎯 Obiettivi
Automazione completa di testing e analisi qualità. I report dettagliati sono disponibili negli **Artifacts** della pipeline.

## 📖 Generalizzazione
Per garantire la scalabilità del sistema su dataset di grandi dimensioni, si è introdotto un modulo di Dynamic Linking (linker.py). 
Invece di basarsi su nomi di file statici, il sistema scansiona il contenuto dei test per identificare la Unit Under Test più citata tramite analisi delle occorrenze. Questo approccio "N-a-1" permette di mappare centinaia di test diversi alla corretta classe di produzione, rendendo l'automazione robusta ed indipendente dal naming dei file.