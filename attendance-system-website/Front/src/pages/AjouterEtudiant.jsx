import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import Layout from "../components/Layout";

export default function AjouterEtudiant() {
    const [nom, setNom] = useState("");
    const [prenom, setPrenom] = useState("");
    const [code, setCode] = useState("");
    const [classeId, setClasseId] = useState("");
    const [classes, setClasses] = useState([]);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const profId = localStorage.getItem("professeurId");
        if (profId) {
            axios.get(`http://localhost:8080/api/classes/professeur/${profId}`)
                .then(res => setClasses(res.data))
                .catch(err => console.error(err));
        }
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            await axios.post("http://localhost:8080/api/etudiants", {
                nom,
                prenom,
                code,
                classeId,
            });
            navigate("/etudiants"); // Rediriger vers la liste des étudiants
        } catch (err) {
            console.error(err);
            alert("Erreur lors de l'ajout de l'étudiant.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Layout>
            <h1 className="text-2xl font-bold mb-6">Ajouter un Étudiant</h1>
            <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow">
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Nom</label>
                    <input
                        type="text"
                        value={nom}
                        onChange={(e) => setNom(e.target.value)}
                        className="w-full p-2 border border-gray-300 rounded-md"
                        required
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Prénom</label>
                    <input
                        type="text"
                        value={prenom}
                        onChange={(e) => setPrenom(e.target.value)}
                        className="w-full p-2 border border-gray-300 rounded-md"
                        required
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Code Étudiant</label>
                    <input
                        type="text"
                        value={code}
                        onChange={(e) => setCode(e.target.value)}
                        className="w-full p-2 border border-gray-300 rounded-md"
                        required
                    />
                </div>
                <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Classe</label>
                    <select
                        value={classeId}
                        onChange={(e) => setClasseId(e.target.value)}
                        className="w-full p-2 border border-gray-300 rounded-md"
                        required
                    >
                        <option value="">Sélectionner une classe</option>
                        {classes.map(classe => (
                            <option key={classe.id} value={classe.id}>{classe.nom}</option>
                        ))}
                    </select>
                </div>
                <div className="flex justify-end">
                    <button
                        type="submit"
                        className="bg-red-600 text-white px-4 py-2 rounded-md hover:bg-red-700 transition-colors"
                        disabled={loading}
                    >
                        {loading ? "Ajout en cours..." : "Ajouter"}
                    </button>
                </div>
            </form>
        </Layout>
    );
}
