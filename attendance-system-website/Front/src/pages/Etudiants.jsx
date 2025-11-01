// src/pages/Etudiants.jsx
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import Layout from "../components/Layout";
import { Dialog } from "@headlessui/react";
import { useNavigate } from 'react-router-dom';


import { Users, Loader2, X } from "lucide-react";


export default function Etudiants() {
    const navigate = useNavigate();
    const [search, setSearch] = useState("");
    const [classeSelectionnee, setClasseSelectionnee] = useState("");
    const [selectedStudent, setSelectedStudent] = useState(null);
    const [etudiants, setEtudiants] = useState([]);
    const [shouldRefresh, setShouldRefresh] = useState(false);
    const [classes, setClasses] = useState([]);
    const [loading, setLoading] = useState(false);
    const [loadingClasses, setLoadingClasses] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [file, setFile] = useState(null);
    const [selectedClassForImport, setSelectedClassForImport] = useState("");
    const filteredEtudiants = etudiants.filter(etudiant =>
        etudiant.nom.toLowerCase().includes(search.toLowerCase()) ||
        etudiant.prenom.toLowerCase().includes(search.toLowerCase())
    );
    useEffect(() => {
        const fetchEtudiants = async () => {
            try {
                const response = await axios.get("http://localhost:8080/api/etudiants");
                setEtudiants(response.data);
            } catch (err) {
                console.error(err);
            }
        };
        fetchEtudiants();
    }, []);
    const handleMarquerAbsence = async (etudiantId) => {
        try {
            const response = await axios.post(
                `http://localhost:8080/api/absences/etudiants/${etudiantId}`
            );

            // Mise à jour ciblée de l'étudiant
            setEtudiants(prev => prev.map(e =>
                e.id === etudiantId
                    ? {
                        ...e,
                        nbAbsencesJustifiees: response.data.nbAbsencesJustifiees,
                        nbAbsencesNonJustifiees: response.data.nbAbsencesNonJustifiees
                    }
                    : e
            ));

        } catch (err) {
            console.error(err);
            alert("Erreur lors du marquage de l'absence");
        }
    };


    const handleFileUpload = async () => {
        if (!file || !selectedClassForImport) return;

        const formData = new FormData();
        formData.append("file", file);
        formData.append("classeId", selectedClassForImport);

        try {
            await axios.post("http://localhost:8080/api/etudiants/importer", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });
            alert("Importation réussie !");
            setIsModalOpen(false);
            setFile(null);
            setSelectedClassForImport("");
            // Recharger la liste si nécessaire
        } catch (err) {
            console.error(err);
            alert("Erreur lors de l'importation.");
        }
    };

    // Récupérer les classes du professeur connecté
    useEffect(() => {
        const profId = localStorage.getItem("professeurId");
        if (profId) {
            setLoadingClasses(true);
            axios.get(`http://localhost:8080/api/classes/professeur/${profId}`)
                .then((res) => {
                    setClasses(res.data);
                    setLoadingClasses(false);
                })
                .catch((err) => {
                    console.error(err);
                    setLoadingClasses(false);
                });
        }
    }, []);

    // Récupérer les étudiants quand une classe est sélectionnée
    useEffect(() => {
        if (classeSelectionnee) {
            setLoading(true);
            axios.get(`http://localhost:8080/api/etudiants/classe/${classeSelectionnee}`)
                .then((res) => {
                    setEtudiants(res.data);
                    setLoading(false);
                })
                .catch((err) => {
                    console.error(err);
                    setLoading(false);
                });
        } else {
            setEtudiants([]);
        }
    }, [classeSelectionnee]);

    return (
        <Layout>
            <h1 className="text-2xl font-bold mb-6">Gestion des Étudiants</h1>

            <div className="bg-white p-6 rounded-lg shadow mb-6">
                <h2 className="text-xl font-semibold mb-4">Liste des étudiants</h2>
                <p className="text-sm text-gray-600 mb-4">Sélectionnez une classe pour voir ses étudiants.</p>

                <div className="mb-6">
                    <label htmlFor="classe" className="block text-sm font-medium text-gray-700 mb-2">Classe</label>
                    <select
                        id="classe"
                        className="w-full p-2 border border-gray-300 rounded-md focus:ring-red-500 focus:border-red-500"
                        value={classeSelectionnee}
                        onChange={(e) => setClasseSelectionnee(e.target.value)}
                        disabled={loadingClasses}
                    >
                        <option value="">Sélectionner une classe</option>
                        {classes.map(classe => (
                            <option key={classe.id} value={classe.id}>{classe.nom}</option>
                        ))}
                    </select>
                    {loadingClasses && (
                        <div className="mt-2 flex items-center text-sm text-gray-500">
                            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                            Chargement des classes...
                        </div>
                    )}
                </div>

                {loading ? (
                    <div className="flex justify-center py-8">
                        <Loader2 className="h-8 w-8 animate-spin text-red-500" />
                    </div>
                ) : classeSelectionnee && etudiants.length > 0 ? (
                    <div className="overflow-x-auto">
                        <input
                            type="text"
                            placeholder="Rechercher par nom ou prénom..."
                            value={search}
                            onChange={(e) => setSearch(e.target.value)}
                            className="px-3 py-2 border rounded-md text-sm w-full sm:w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                            <tr>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Code</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Nom</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Prénom</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Absences</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Marquer Absence</th>
                                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Justification</th>

                            </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                            {filteredEtudiants.map(etudiant => (
                                <tr key={etudiant.id}>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{etudiant.code}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{etudiant.nom}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{etudiant.prenom}</td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                        <Link to={`/etudiants/${etudiant.id}`} className="text-red-600 hover:text-red-800 mr-3">Modifier</Link>
                                        <button
                                            className="text-gray-600 hover:text-gray-800"
                                            onClick={async () => {
                                                const confirm = window.confirm("Voulez-vous vraiment supprimer cet étudiant ?");
                                                if (confirm) {
                                                    try {
                                                        await axios.delete(`http://localhost:8080/api/etudiants/${etudiant.id}`);
                                                        // Rafraîchir la liste après suppression
                                                        setEtudiants(etudiants.filter(e => e.id !== etudiant.id));
                                                    } catch (err) {
                                                        console.error(err);
                                                        alert("Erreur lors de la suppression.");
                                                    }
                                                }
                                            }}
                                        >
                                            Supprimer
                                        </button>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap">
                                        <div className="flex flex-col">
        <span className="text-green-600">
            ✓ {etudiant.nbAbsencesJustifiees ?? 0} justifiées
        </span>
                                            <span className="text-red-600">
            ✗ {etudiant.nbAbsencesNonJustifiees ?? 0} non justifiées
        </span>
                                            <span className="text-gray-500 text-xs">
            Total: {(etudiant.nbAbsencesJustifiees ?? 0) + (etudiant.nbAbsencesNonJustifiees ?? 0)}
        </span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                        <button
                                            onClick={() => handleMarquerAbsence(etudiant.id)}
                                            className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 transition-colors"
                                        >
                                            Marquer
                                        </button>
                                    </td>
                                    <td>
                                        <button
                                            onClick={() => navigate(`/justifications/${etudiant.id}`)}
                                            className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 transition-colors"
                                        >
                                            Gérer les justifications
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                ) : classeSelectionnee ? (
                    <div className="text-center py-8 text-gray-500">
                        <Users className="mx-auto h-12 w-12 text-gray-400" />
                        <p className="mt-2">Aucun étudiant trouvé dans cette classe.</p>
                    </div>
                ) : (
                    <div className="text-center py-8 text-gray-500">
                        <Users className="mx-auto h-12 w-12 text-gray-400" />
                        <p className="mt-2">Veuillez sélectionner une classe pour voir les étudiants.</p>
                    </div>
                )}
            </div>

            <div className="flex justify-end space-x-4">
                <Link
                    to="/etudiants/ajouter"
                    className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors"
                >
                    Ajouter un étudiant
                </Link>
                <button
                    onClick={() => setIsModalOpen(true)}
                    className="bg-gray-600 text-white px-4 py-2 rounded-md hover:bg-gray-700 transition-colors"
                >
                    Importer
                </button>
            </div>


            {/* Popup Modal */}
            {isModalOpen && (
                <div className="fixed inset-0 z-50 bg-black bg-opacity-40 flex items-center justify-center">
                    <div className="bg-white rounded-lg p-6 w-full max-w-md shadow-lg relative">
                        <button
                            onClick={() => setIsModalOpen(false)}
                            className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
                        >
                            <X className="w-5 h-5" />
                        </button>

                        <h2 className="text-lg font-semibold mb-4">Importer un fichier Excel</h2>

                        <div className="mb-4">
                            <label className="block mb-1 text-sm font-medium">Sélectionner une classe</label>
                            <select
                                className="w-full border border-gray-300 p-2 rounded"
                                value={selectedClassForImport}
                                onChange={(e) => setSelectedClassForImport(e.target.value)}
                            >
                                <option value="">-- Choisir une classe --</option>
                                {classes.map(classe => (
                                    <option key={classe.id} value={classe.id}>{classe.nom}</option>
                                ))}
                            </select>
                        </div>

                        <div className="mb-4">
                            <label className="block mb-1 text-sm font-medium">Fichier Excel</label>
                            <input
                                type="file"
                                accept=".xlsx, .xls"
                                onChange={(e) => setFile(e.target.files[0])}
                                className="w-full border border-gray-300 p-2 rounded"
                            />
                        </div>

                        <div className="flex justify-end space-x-3">
                            <button
                                onClick={() => setIsModalOpen(false)}
                                className="bg-gray-300 text-gray-700 px-4 py-2 rounded hover:bg-gray-400"
                            >
                                Annuler
                            </button>
                            <button
                                onClick={handleFileUpload}
                                className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                            >
                                Importer
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </Layout>
    );
}