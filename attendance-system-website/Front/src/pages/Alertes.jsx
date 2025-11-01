import React, { useState, useEffect } from "react";
import axios from "axios";
import Layout from "../components/Layout";
import { Search, Loader2 } from "lucide-react";

export default function Alertes() {
    const [classeSelectionnee, setClasseSelectionnee] = useState("");
    const [search, setSearch] = useState("");
    const [etudiants, setEtudiants] = useState([]);
    const [classes, setClasses] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        // Récupérer la liste des classes
        const profId = localStorage.getItem("professeurId");
        if (profId) {
            axios.get(`http://localhost:8080/api/classes/professeur/${profId}`)
                .then((res) => setClasses(res.data));
        }
    }, []);

    useEffect(() => {
        // Récupérer les alertes d'absences
        if (classeSelectionnee) {
            setLoading(true);
            axios.get(`http://localhost:8080/api/absences/alertes`, {
                params: {
                    seuil: 3, // Seuil fixé à 3 comme dans votre exemple
                    classeId: classeSelectionnee
                }
            })
                .then((res) => {
                    setEtudiants(res.data);
                })
                .finally(() => setLoading(false));
        }
    }, [classeSelectionnee]);

    // Filtrer les étudiants selon la recherche
    const filteredEtudiants = etudiants.filter(etudiant =>
        `${etudiant.prenom} ${etudiant.nom}`.toLowerCase().includes(search.toLowerCase()) ||
        (etudiant.classeId && etudiant.classeId.toLowerCase().includes(search.toLowerCase()))
    );

    return (
        <Layout>
            <h1 className="text-2xl font-bold mb-6">Alertes d'Absences</h1>

            <div className="bg-white p-6 rounded-lg shadow mb-6">
                <h2 className="text-xl font-semibold mb-4">Étudiants en alerte</h2>
                <p className="text-sm text-gray-600 mb-4">
                    Les étudiants suivants ont 3 absences non justifiées ou plus
                </p>

                <div className="mb-6 grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Classe</label>
                        <select
                            className="w-full p-2 border border-gray-300 rounded-md"
                            value={classeSelectionnee}
                            onChange={(e) => setClasseSelectionnee(e.target.value)}
                        >
                            <option value="">Toutes les classes</option>
                            {classes.map(classe => (
                                <option key={classe.id} value={classe.id}>{classe.nom}</option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Rechercher</label>
                        <div className="relative">
                            <input
                                type="text"
                                className="w-full p-2 pl-10 border border-gray-300 rounded-md"
                                placeholder="Nom, prénom ou classe"
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                            />
                            <Search className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
                        </div>
                    </div>
                </div>

                {loading ? (
                    <div className="flex justify-center py-8">
                        <Loader2 className="h-8 w-8 animate-spin text-red-500" />
                    </div>
                ) : filteredEtudiants.length > 0 ? (
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Étudiant</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Classe</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Absences non justifiées</th>
                            </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                            {filteredEtudiants.map(etudiant => {
                                const classe = classes.find(c => c.id === etudiant.classeId);
                                return (
                                    <tr key={etudiant.id} className="hover:bg-gray-50">
                                        <td className="px-6 py-4 whitespace-nowrap font-medium">
                                            {etudiant.prenom} {etudiant.nom}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap">
                                            {classe?.nom || etudiant.classeId}
                                        </td>
                                        <td className="px-6 py-4 whitespace-nowrap text-red-600 font-medium">
                                            {etudiant.nbAbsencesNonJustifiees}
                                        </td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </table>
                    </div>
                ) : classeSelectionnee ? (
                    <div className="text-center py-8 text-gray-500">
                        <p>Aucun étudiant avec 3 absences ou plus dans cette classe.</p>
                    </div>
                ) : (
                    <div className="text-center py-8 text-gray-500">
                        <p>Veuillez sélectionner une classe pour voir les alertes.</p>
                    </div>
                )}
            </div>
        </Layout>
    );
}